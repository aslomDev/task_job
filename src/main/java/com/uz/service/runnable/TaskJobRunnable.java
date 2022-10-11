package com.uz.service.runnable;

import com.uz.entity.JobTask;
import com.uz.service.CronService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Component
@RequiredArgsConstructor
public class TaskJobRunnable implements Runnable{
    private JobTask jobTask;
    private final CronService cronService;

    @Override
    public void run() {
        cronService.executeTask(jobTask);
    }
}
