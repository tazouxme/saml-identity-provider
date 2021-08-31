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

import com.tazouxme.idp.dao.query.SessionQueries;
import com.tazouxme.idp.model.base.AbstractModel;

@Entity
@Table(name = "tz_session", 
	uniqueConstraints = {
		@UniqueConstraint(name = "u_session_1", columnNames = { "external_id" }),
		@UniqueConstraint(name = "u_session_2", columnNames = { "organization_external_id", "user_external_id" }),
		@UniqueConstraint(name = "u_session_3", columnNames = { "token" })
	},
	indexes = {
		@Index(name = "i_session_1", unique = true, columnList = "external_id"),
		@Index(name = "i_session_2", unique = true, columnList = "organization_external_id, user_external_id"),
		@Index(name = "i_session_3", unique = true, columnList = "token")
	})
@NamedQueries({
	@NamedQuery(name = SessionQueries.NQ_FIND, query = SessionQueries.FIND)
})
public class Session extends AbstractModel {

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
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Session)) {
			return false;
		}
		
		if (this == obj) {
			return true;
		}
		
		Session a = (Session) obj;
		return getExternalId().equals(a.getExternalId());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getExternalId());
	}

}
