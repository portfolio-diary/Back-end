package com.diary.common.base;


import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private BaseStatus status = BaseStatus.ACTIVE;


    /**
     * 수정 시간 업데이트
     */
    public void updateUpdatedAt(@NotNull LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Status 변경
     * @param status
     */
    public void changeStatus(BaseStatus status) {
        updateUpdatedAt(LocalDateTime.now());
        this.status = (status == BaseStatus.ACTIVE) ? BaseStatus.INACTIVE : BaseStatus.ACTIVE;
    }

}