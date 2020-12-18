package org.tc.financial.state.domain;

import java.util.Date;

import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@MappedSuperclass
public abstract class ModelAudit extends EntityWithUUID{

	@CreatedDate
	private Date createdAt;
	
	@LastModifiedDate
	private Date updateAt;
}
