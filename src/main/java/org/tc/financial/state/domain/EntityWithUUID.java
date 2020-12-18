package org.tc.financial.state.domain;

import java.util.UUID;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Type;

import lombok.Data;

@MappedSuperclass
@Data
public abstract class EntityWithUUID {

	@Id
	@Type(type = "pg-uuid")
	private UUID uuid;
	
	public EntityWithUUID() {
		this.uuid = UUID.randomUUID();
	}
}
