package com.uz.controller;

import com.uz.entity.JobTask;
import com.uz.service.JobTaskService;
import com.uz.service.dto.BaseResponse;
import com.uz.service.dto.JobTaskRequestDTO;
import com.uz.service.impl.CronServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class JobTaskController {

    private final JobTaskService jobTaskService;
    private final CronServiceImpl cronService;



    public JobTaskController(@Qualifier("jobTaskServiceImpl") JobTaskService jobTaskService, CronServiceImpl cronService) {
        this.jobTaskService = jobTaskService;
        this.cronService = cronService;
    }

    @PostMapping("/make/task")
    public ResponseEntity<BaseResponse> makeTask(@RequestBody JobTaskRequestDTO requestDTO){
        return ResponseEntity.ok(jobTaskService.makeTask(requestDTO));
    }

    @PutMapping("/task")
    public ResponseEntity<BaseResponse> updateTask(@RequestBody JobTask requestDTO){
        return ResponseEntity.ok(jobTaskService.updateJobTask(requestDTO));
    }

    @DeleteMapping("/task")
    public ResponseEntity<BaseResponse> deleteTask(@RequestBody JobTask requestDTO){
        return ResponseEntity.ok(jobTaskService.deleteTask(requestDTO));
    }

    @GetMapping("/tasks")
    public ResponseEntity<BaseResponse> getAllTasks(){
        return ResponseEntity.ok(jobTaskService.getAllTask());
    }
}
