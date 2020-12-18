package org.tc.financial.state.domain;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Data
public class StateType extends EntityWithUUID{
	
	@NonNull
	@Column(unique = true, nullable = false, length = 10, updatable = false)
	private String code;
	
	@NonNull
	@Column(nullable = false, length = 150)
	private String label;
	
	@Column(nullable = true)
	private String description;
	
	@Column(nullable = true, length = 25)
	private String icone;
	
	@NonNull
	@Column(nullable = false, unique = true, length = 15)
	private String templateName;
	
	private boolean active;
}
