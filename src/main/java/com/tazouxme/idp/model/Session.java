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

import com.tazouxme.idp.dao.query.SessionQueries;

@Entity
@Table(name = "tz_session")
@NamedQueries({
	@NamedQuery(name = "Session.find", query = SessionQueries.FIND)
})
public class Session {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Session_generator")
	@SequenceGenerator(name = "Session_generator", sequenceName = "Session_sequence", allocationSize = 1)
	@Column(name = "session_id", length = 8, updatable = false, nullable = false)
	private long id;
	
	@Column(name = "external_id", length = 16, updatable = false, nullable = false)
	private String externalId;
	
	@Column(name = "organization_external_id", length = 16, updatable = false, nullable = false)
	private String organizationExternalId;
	
	@Column(name = "user_external_id", length = 16, updatable = false, nullable = false)
	private String userExternalId;
	
	@Column(name = "token", length = 36, updatable = false, nullable = false)
	private String token;
	
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

	public String getOrganizationExternalId() {
		return organizationExternalId;
	}

	public void setOrganizationExternalId(String organizationExternalId) {
		this.organizationExternalId = organizationExternalId;
	}

	public String getUserExternalId() {
		return userExternalId;
	}

	public void setUserExternalId(String userExternalId) {
		this.userExternalId = userExternalId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}

}
