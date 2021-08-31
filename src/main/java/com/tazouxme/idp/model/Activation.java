package com.tazouxme.idp.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.tazouxme.idp.dao.query.ActivationQueries;
import com.tazouxme.idp.model.base.AbstractModel;

@Entity
@Table(name = "tz_activation", 
	uniqueConstraints = {
		@UniqueConstraint(name = "u_activation_1", columnNames = { "external_id" }),
		@UniqueConstraint(name = "u_activation_2", columnNames = { "user_external_id", "organization_external_id" })
	},
	indexes = {
		@Index(name = "i_activation_1", unique = true, columnList = "external_id"),
		@Index(name = "i_activation_2", unique = true, columnList = "user_external_id, organization_external_id")
	})
@NamedQueries({
	@NamedQuery(name = ActivationQueries.NQ_FIND, query = ActivationQueries.FIND),
	@NamedQuery(name = ActivationQueries.NQ_FIND_BY_ID, query = ActivationQueries.FIND_BY_ID),
	@NamedQuery(name = ActivationQueries.NQ_FIND_BY_USER, query = ActivationQueries.FIND_BY_USER)
})
public class Activation extends AbstractModel {

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
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Activation)) {
			return false;
		}
		
		if (this == obj) {
			return true;
		}
		
		Activation a = (Activation) obj;
		return getExternalId().equals(a.getExternalId());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getExternalId());
	}

}
