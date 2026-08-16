package com.cuonglm.ecommerce.backend.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

/**
 * AuditMetadata – Value Object lưu trữ thông tin kiểm toán (Auditing) của Entity.
 *
 * @author cuonglmptit
 * @since Sunday, 16 August 2026
 */
@Embeddable
public class AuditMetadata {
    @CreatedBy
    @Column(name = "created_by_id", updatable = false)
    private Long createdById;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedBy
    @Column(name = "modified_by_id")
    private Long modifiedById;

    @LastModifiedDate
    @Column(name = "modified_at", nullable = false)
    private Instant modifiedAt;

    public AuditMetadata() {
    }

    public AuditMetadata(Long createdById, Instant createdAt, Long modifiedById, Instant modifiedAt) {
        this.createdById = createdById;
        this.createdAt = createdAt;
        this.modifiedById = modifiedById;
        this.modifiedAt = modifiedAt;
    }

    //<editor-fold desc="Getters/Setters">
    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Long getModifiedById() {
        return modifiedById;
    }

    public void setModifiedById(Long modifiedById) {
        this.modifiedById = modifiedById;
    }

    public Instant getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Instant modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
    //</editor-fold>
}
