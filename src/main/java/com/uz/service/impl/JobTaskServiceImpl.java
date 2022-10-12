package com.uz.service.impl;

import com.uz.entity.JobTask;
import com.uz.entity.enums.Status;
import com.uz.repository.JobTaskRepository;
import com.uz.service.CronService;
import com.uz.service.JobTaskService;
import com.uz.service.dto.BaseResponseData;
import com.uz.service.dto.BaseResponse;
import com.uz.service.dto.JobTaskRequestDTO;
import com.uz.service.runnable.TaskJobRunnable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service
@Transactional
public class JobTaskServiceImpl implements JobTaskService {
    private final JobTaskRepository jobTaskRepository;
    private final TaskScheduler executor;
    private final CronService cronService;
    private TaskJobRunnable runnable;

    Map<Long, ScheduledFuture<?>> jobsMap = new HashMap<>();

    public JobTaskServiceImpl(JobTaskRepository jobTaskRepository, @Qualifier("taskScheduler") TaskScheduler executor, CronService cronService) {
        this.jobTaskRepository = jobTaskRepository;
        this.executor = executor;
        this.cronService = cronService;
    }

    @Override
    public BaseResponse getAllTask() {
        return new BaseResponseData<>(jobTaskRepository.findAll());
    }

    @PostConstruct
    private void executeAllActiveTaskAfterRestartingServer(){
        jobTaskRepository.findAll()
                .forEach(jobTask -> {
                    if (jobTask.getStatus().equals(Status.ENABLE)){
                        runnable = new TaskJobRunnable(cronService);
                        runnable.setJobTask(jobTask);
                        ScheduledFuture<?> schedule = executor.schedule(runnable, new CronTrigger(jobTask.getCronExpression(), TimeZone.getTimeZone(ZoneId.systemDefault().getId())));
                        jobsMap.put(jobTask.getId(), schedule);
                    }
                });
    }

    @Override
    public BaseResponse updateJobTask(JobTask jobTask) {
        Optional<JobTask> hasElement = jobTaskRepository.findById(jobTask.getId());
        if (hasElement.isPresent()){
            JobTask saved = jobTaskRepository.saveAndFlush(jobTask);
            if (saved.getStatus().equals(Status.DISABLE)){
                cancelJob(saved.getId());
            }else {
                runnable = new TaskJobRunnable(cronService);
                runnable.setJobTask(saved);
                ScheduledFuture<?> schedule = executor.schedule(runnable, new CronTrigger(jobTask.getCronExpression(), TimeZone.getTimeZone(ZoneId.systemDefault().getId())));
                jobsMap.put(saved.getId(), schedule);
            }
            return new BaseResponseData<>(saved);
        }
        return new BaseResponse(false, "not found in db");
    }

    private void cancelJob(Long id) {
        if (id != null && jobsMap.containsKey(id)){
            ScheduledFuture<?> scheduledFuture = jobsMap.get(id);
            scheduledFuture.cancel(true);
            jobsMap.put(id, null);
            log.info("Task is canceled: {}", jobsMap.get(id));
        } else {
            log.info("Task not found");
        }
    }

    @Override
    public BaseResponse makeTask(JobTaskRequestDTO requestDTO) {
        JobTask jobTask = new JobTask();
        jobTask.setCronExpression(requestDTO.getCronExpression());
        jobTask.setData(requestDTO.getData());
        JobTask saved = jobTaskRepository.saveAndFlush(jobTask);
        runnable = new TaskJobRunnable(cronService);
        runnable.setJobTask(saved);
        ScheduledFuture<?> schedule = executor.schedule(runnable, new CronTrigger(jobTask.getCronExpression(), TimeZone.getTimeZone(ZoneId.systemDefault().getId())));
        jobsMap.put(saved.getId(), schedule);
        if (saved.getId() != null) {
            return new BaseResponseData<>(saved);
        }
        return new BaseResponse(false, "lost connection with database");
    }

    @Override
    public BaseResponse deleteTask(JobTask requestDTO) {
        Optional<JobTask> hasElement = jobTaskRepository.findById(requestDTO.getId());
        if (hasElement.isPresent()){
            jobTaskRepository.delete(requestDTO);
            cancelJob(requestDTO.getId());
            return new BaseResponse(true, "deleted");
        }
        return new BaseResponse(false, "not found in db");
    }
}
