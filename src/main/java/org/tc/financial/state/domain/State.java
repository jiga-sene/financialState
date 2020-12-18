package org.tc.financial.state.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

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
public class State extends ModelAudit {

	@NonNull
	@Column(nullable = false, updatable = false)
	private String year;

	@Column(nullable = true)
	private String month;

	@NonNull
	@Column(nullable = false)
	private String username;
	
	@NonNull
	@Column(nullable = false, columnDefinition="TEXT")
	private String data;
	
	@ManyToOne
	@JoinColumn(name = "customerSate", nullable = false)
	private Customer customer;

	@ManyToOne
	@JoinColumn(name = "stateType", nullable = false)
	private StateType stateType;
}
