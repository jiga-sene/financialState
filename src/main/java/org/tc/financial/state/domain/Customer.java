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
public class Customer extends ModelAudit{
	
	@NonNull
	@Column(nullable = false, unique = true)
	private String code;
	
	@Column(nullable = true)
	private String fullName;
}
