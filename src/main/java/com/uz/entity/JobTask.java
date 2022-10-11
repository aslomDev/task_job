package com.uz.entity;

import com.uz.entity.enums.Status;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity(name = "job_task")
public class JobTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cron_expression")
    private String cronExpression;

    @Column(name = "data")
    private String data;

    @Column(name = "status")
    private Status status = Status.ENABLE;

    @Override
    public boolean equals(Object params) {
        if (this == params) return true;
        if (params == null || Hibernate.getClass(this) != Hibernate.getClass(params))
            return false;
        JobTask jobTask = (JobTask) params;
        return id != null && Objects.equals(id, jobTask.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

