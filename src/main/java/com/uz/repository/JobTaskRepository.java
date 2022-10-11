package com.uz.repository;

import com.uz.entity.JobTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobTaskRepository extends JpaRepository<JobTask, Long> {
}
