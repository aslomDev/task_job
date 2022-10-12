package com.uz.service.impl;

import com.uz.entity.JobTask;
import com.uz.entity.enums.Status;
import com.uz.service.CronService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Locale;


@Slf4j
@Service
@Transactional
public class CronServiceImpl implements CronService {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void executeTask(JobTask jobTask) {
        log.info("Starting task: {}", jobTask);
        if (jobTask == null || jobTask.getData() == null || jobTask.getStatus() == null || jobTask.getStatus().equals(Status.DISABLE)) return;
        log.info("Task with param: {}", jobTask.getData());
        if (jobTask.getData().trim().toLowerCase(Locale.ROOT).startsWith("update") || jobTask.getData().trim().toLowerCase(Locale.ROOT).startsWith("insert") || jobTask.getData().trim().toLowerCase(Locale.ROOT).startsWith("call")){
            Query nativeQuery = entityManager.createNativeQuery(jobTask.getData());
            log.info("Native query: {}", nativeQuery.toString());
            nativeQuery.executeUpdate();
        }else {
            List<?> resultList = entityManager.createNativeQuery(jobTask.getData()).getResultList();
            if (!resultList.isEmpty()){
//                log.info("ResultSet: {},", resultList.toArray());
            }
        }
        log.info("End task");
    }
}
