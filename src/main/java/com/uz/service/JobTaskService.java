package com.uz.service;

import com.uz.entity.JobTask;
import com.uz.service.dto.BaseResponse;
import com.uz.service.dto.JobTaskRequestDTO;

public interface JobTaskService {
    BaseResponse getAllTask();

    BaseResponse updateJobTask(JobTask jobTask);

    BaseResponse makeTask(JobTaskRequestDTO requestDTO);

    BaseResponse deleteTask(JobTask requestDTO);
}
