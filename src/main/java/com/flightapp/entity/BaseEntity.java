package com.flightapp.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract  class BaseEntity {
	
	@CreatedDate
	@Column(name ="created_at", nullable=false , updatable=false)
	private LocalDateTime createdAt;
	
	@LastModifiedDate
	@Column(name="updated_at")
	private LocalDateTime updatedAt;
	
	@PrePersist
	protected void onCreate() {
		createdAt=LocalDateTime.now();
		updatedAt=LocalDateTime.now();
	}
	@PreUpdate
	protected void onUpdate() {
		updatedAt=LocalDateTime.now();
	}
	
}
