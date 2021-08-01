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

import com.tazouxme.idp.dao.query.AccessQueries;

@Entity
@Table(name = "tz_access")
@NamedQueries({
	@NamedQuery(name = "Access.findByExternalId", query = AccessQueries.FIND_BY_ID),
	@NamedQuery(name = "Access.findByUser", query = AccessQueries.FIND_BY_USER),
	@NamedQuery(name = "Access.findByOrganization", query = AccessQueries.FIND_BY_ORGANIZATION)
})
public class Access {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Access_generator")
	@SequenceGenerator(name = "Access_generator", sequenceName = "Access_sequence", allocationSize = 1)
	@Column(name = "access_id", length = 8, updatable = false, nullable = false)
	private long id;
	
	@Column(name = "external_id", length = 16, updatable = false, nullable = false)
	private String externalId;
	
	@Column(name = "urn", length = 32, updatable = false, nullable = false)
	private String urn;
	
	@Column(name = "access_type", length = 4, updatable = false, nullable = false)
	private String accessType;
	
	@Column(name = "access_key", length = 16, updatable = false, nullable = false)
	private String accessKey;
	
	@Column(name = "role", length = 16, updatable = true, nullable = false)
	private String role;
	
	@Column(name = "enabled", length = 1, updatable = true, nullable = false)
	private boolean enabled;
	
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

	public String getUrn() {
		return urn;
	}

	public void setUrn(String urn) {
		this.urn = urn;
	}

	public String getAccessType() {
		return accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}

}
