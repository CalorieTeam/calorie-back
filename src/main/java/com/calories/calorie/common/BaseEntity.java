package com.calories.calorie.common;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public abstract class BaseEntity {
    //생성날짜
    @CreatedDate
    private LocalDateTime createdDt;

    //수정날짜
    @LastModifiedDate
    private LocalDateTime updatedDt;

    @PrePersist
    protected void prePersist() {
        this.createdDt = LocalDateTime.now();
        this.updatedDt = LocalDateTime.now();
    }

    @PreUpdate
    protected void preUpdate() {
        this.updatedDt = LocalDateTime.now();
    }

}
