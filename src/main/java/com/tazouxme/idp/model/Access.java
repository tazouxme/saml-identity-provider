package com.tazouxme.idp.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tazouxme.idp.dao.query.AccessQueries;
import com.tazouxme.idp.model.base.AbstractModel;

@Entity
@Table(name = "tz_access", 
	uniqueConstraints = {
		@UniqueConstraint(name = "u_access_1", columnNames = { "external_id" }),
		@UniqueConstraint(name = "u_access_2", columnNames = { "organization_id", "user_id", "application_id", "role_id" })
	},
	indexes = {
		@Index(name = "i_access_1", unique = true, columnList = "external_id"),
		@Index(name = "i_access_2", unique = true, columnList = "organization_id, user_id, application_id, role_id")
	}
)
@NamedQueries({
	@NamedQuery(name = AccessQueries.NQ_FIND_BY_ID, query = AccessQueries.FIND_BY_ID),
	@NamedQuery(name = AccessQueries.NQ_FIND_BY_URN, query = AccessQueries.FIND_BY_URN),
	@NamedQuery(name = AccessQueries.NQ_FIND_BY_USER, query = AccessQueries.FIND_BY_USER),
	@NamedQuery(name = AccessQueries.NQ_FIND_BY_USER_AND_URN, query = AccessQueries.FIND_BY_USER_AND_URN),
	@NamedQuery(name = AccessQueries.NQ_FIND_BY_ORGANIZATION, query = AccessQueries.FIND_BY_ORGANIZATION)
})
public class Access extends AbstractModel {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Access_generator")
	@SequenceGenerator(name = "Access_generator", sequenceName = "Access_sequence", allocationSize = 1)
	@Column(name = "access_id", length = 8, updatable = false, nullable = false)
	private long id;
	
	@Column(name = "external_id", length = 16, updatable = false, nullable = false)
	private String externalId;
	
	@Column(name = "enabled", length = 1, updatable = true, nullable = false)
	private boolean enabled;

	@JsonIgnoreProperties("accesses")
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", foreignKey = @ForeignKey(name = "fk_tz_access_organization"))
	private Organization organization;

	@JsonIgnoreProperties("accesses")
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_tz_access_user"))
	private User user;

	@JsonIgnoreProperties("accesses")
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", foreignKey = @ForeignKey(name = "fk_tz_access_application"))
	private Application application;

	@JsonIgnoreProperties("accesses")
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "fk_tz_access_role"))
	private Role role;
	
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

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Access)) {
			return false;
		}
		
		if (this == obj) {
			return true;
		}
		
		Access a = (Access) obj;
		return getExternalId().equals(a.getExternalId());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getExternalId());
	}

}
