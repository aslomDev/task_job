package com.uz.service.runnable;

import com.uz.entity.JobTask;
import com.uz.service.CronService;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class TaskJobRunnable implements Runnable{
    private JobTask jobTask;
    private final CronService cronService;

    public TaskJobRunnable(CronService cronService) {
        this.cronService = cronService;
    }


    @Override
    public void run() {
        cronService.executeTask(jobTask);
    }
}
