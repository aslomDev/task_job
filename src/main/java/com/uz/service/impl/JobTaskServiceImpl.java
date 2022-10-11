package com.uz.service.impl;

import com.uz.entity.JobTask;
import com.uz.repository.JobTaskRepository;
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
import java.time.ZoneId;
import java.util.Optional;
import java.util.TimeZone;

@Slf4j
@Service
@Transactional
public class JobTaskServiceImpl implements JobTaskService {
    private final JobTaskRepository jobTaskRepository;
    private final TaskScheduler executor;
    private final TaskJobRunnable runnable;

    public JobTaskServiceImpl(JobTaskRepository jobTaskRepository, @Qualifier("taskScheduler") TaskScheduler executor, TaskJobRunnable runnable) {
        this.jobTaskRepository = jobTaskRepository;
        this.executor = executor;
        this.runnable = runnable;
    }

    @Override
    public BaseResponse getAllTask() {
        return new BaseResponseData<>(jobTaskRepository.findAll());
    }

    @Override
    public BaseResponse updateJobTask(JobTask jobTask) {
        Optional<JobTask> hasElement = jobTaskRepository.findById(jobTask.getId());
        if (hasElement.isPresent()){
            JobTask saved = jobTaskRepository.save(jobTask);
            return new BaseResponseData<>(saved);
        }
        return new BaseResponse(false, "not found in db");
    }

    @Override
    public BaseResponse makeTask(JobTaskRequestDTO requestDTO) {
        JobTask jobTask = new JobTask();
        jobTask.setCronExpression(requestDTO.getCronExpression());
        jobTask.setData(requestDTO.getData());
        JobTask saved = jobTaskRepository.saveAndFlush(jobTask);
        runnable.setJobTask(saved);
        executor.schedule(runnable, new CronTrigger(jobTask.getCronExpression(), TimeZone.getTimeZone(ZoneId.systemDefault().getId())));

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
            return new BaseResponse(true, "deleted");
        }
        return new BaseResponse(false, "not found in db");
    }
}
