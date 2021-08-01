package com.tazouxme.idp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.tazouxme.idp.dao.query.ActivationQueries;

@Entity
@Table(name = "tz_activation")
@NamedQueries({
	@NamedQuery(name = "Activation.find", query = ActivationQueries.FIND),
	@NamedQuery(name = "Activation.findByExternalId", query = ActivationQueries.FIND_BY_ID)
})
public class Activation {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Activation_generator")
	@SequenceGenerator(name = "Activation_generator", sequenceName = "Activation_sequence", allocationSize = 1)
	@Column(name = "activation_id", length = 8, updatable = false, nullable = false)
	private long id;
	
	@Column(name = "external_id", length = 16, updatable = false, nullable = false)
	private String externalId;
	
	@Column(name = "user_external_id", length = 16, updatable = false, nullable = false)
	private String userExternalId;
	
	@Column(name = "organization_external_id", length = 16, updatable = false, nullable = false)
	private String organizationExternalId;
	
	@Column(name = "step", length = 16, updatable = false, nullable = false)
	private String step;
	
	@Column(name = "creation_date", length = 16, updatable = false, nullable = false)
	private long creationDate;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	
	public String getUserExternalId() {
		return userExternalId;
	}
	
	public void setUserExternalId(String userExternalId) {
		this.userExternalId = userExternalId;
	}
	
	public String getOrganizationExternalId() {
		return organizationExternalId;
	}
	
	public void setOrganizationExternalId(String organizationExternalId) {
		this.organizationExternalId = organizationExternalId;
	}
	
	public String getStep() {
		return step;
	}
	
	public void setStep(String step) {
		this.step = step;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}

}
