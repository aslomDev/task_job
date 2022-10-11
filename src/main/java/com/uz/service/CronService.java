package com.uz.service;

import com.uz.entity.JobTask;

public interface CronService {
    void executeTask(JobTask jobTask);
}
