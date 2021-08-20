package com.tazouxme.idp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tazouxme.idp.dao.query.FederationQueries;

@Entity
@Table(name = "tz_federation")
@NamedQueries({
	@NamedQuery(name = FederationQueries.NQ_FIND_BY_URN, query = FederationQueries.FIND_BY_URN),
	@NamedQuery(name = FederationQueries.NQ_FIND_BY_USER, query = FederationQueries.FIND_BY_USER),
	@NamedQuery(name = FederationQueries.NQ_FIND_BY_USER_AND_URN, query = FederationQueries.FIND_BY_USER_AND_URN),
	@NamedQuery(name = FederationQueries.NQ_FIND_BY_ORGANIZATION, query = FederationQueries.FIND_BY_ORGANIZATION)
})
public class Federation {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Federation_generator")
	@SequenceGenerator(name = "Federation_generator", sequenceName = "Federation_sequence", allocationSize = 1)
	@Column(name = "federation_id", length = 8, updatable = false, nullable = false)
	private long id;
	
	@Column(name = "external_id", length = 16, updatable = false, nullable = false)
	private String externalId;
	
	@Column(name = "enabled", length = 1, updatable = true, nullable = false)
	private boolean enabled;
	
	@Column(name = "creation_date", length = 16, updatable = false, nullable = false)
	private long creationDate;

	@JsonIgnoreProperties("federations")
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", foreignKey = @ForeignKey(name = "fk_tz_federation_organization"))
	private Organization organization;

	@JsonIgnoreProperties("federations")
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_tz_federation_user"))
	private User user;

	@JsonIgnoreProperties("federations")
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", foreignKey = @ForeignKey(name = "fk_tz_federation_application"))
	private Application application;

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

	public long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
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

}
