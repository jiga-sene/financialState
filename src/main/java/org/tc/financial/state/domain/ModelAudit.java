package org.tc.financial.state.domain;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.Data;

@MappedSuperclass
@Data
public abstract class ModelAudit extends EntityWithUUID {

	private LocalDateTime createdAt;
	private LocalDateTime updateAt;

	@PrePersist
	void prePersist() {
		this.createdAt = LocalDateTime.now();
	}

	@PreUpdate
	void preUpdate() {
		this.updateAt = LocalDateTime.now();
	}
}
