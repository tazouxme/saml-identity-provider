package com.tazouxme.idp.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tazouxme.idp.dao.query.ApplicationQueries;

@Entity
@Table(name = "tz_application")
@NamedQueries({
	@NamedQuery(name = ApplicationQueries.NQ_FIND_ALL, query = ApplicationQueries.FIND_ALL),
	@NamedQuery(name = ApplicationQueries.NQ_FIND_BY_URN, query = ApplicationQueries.FIND_BY_URN),
	@NamedQuery(name = ApplicationQueries.NQ_FIND_BY_EXTERNAL_ID, query = ApplicationQueries.FIND_BY_EXTERNAL_ID)
})
public class Application {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Application_generator")
	@SequenceGenerator(name = "Application_generator", sequenceName = "Application_sequence", allocationSize = 1)
	@Column(name = "application_id", length = 8, updatable = false, nullable = false)
	private long id;
	
	@Column(name = "external_id", length = 16, updatable = false, nullable = false)
	private String externalId;
	
	@Column(name = "urn", length = 32, updatable = false, nullable = false)
	private String urn;
	
	@Audited
	@Column(name = "name", length = 50, updatable = true, nullable = false)
	private String name;

	@Audited
	@Column(name = "description", length = 200, updatable = true, nullable = true)
	private String description;

	@Audited
	@Column(name = "assertion_url", length = 200, updatable = true)
	private String assertionUrl;

	@Audited
	@Column(name = "logout_url", length = 200, updatable = true)
	private String logoutUrl;

	@JsonIgnoreProperties("applications")
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", foreignKey = @ForeignKey(name = "fk_tz_application_organization"))
	private Organization organization;
	
	@JsonIgnoreProperties("applications")
	@ManyToMany(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST })
    @JoinTable(name = "tz_application_claims", joinColumns = { @JoinColumn(name = "application_id") }, inverseJoinColumns = { @JoinColumn(name = "claim_id") })
	private Set<Claim> claims = new HashSet<>();
	
	@JsonIgnoreProperties("application")
	@OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Access> accesses = new HashSet<>();
	
	@JsonIgnoreProperties("application")
	@OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Federation> federations = new HashSet<>();

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAssertionUrl() {
		return assertionUrl;
	}

	public void setAssertionUrl(String assertionUrl) {
		this.assertionUrl = assertionUrl;
	}
	
	public String getLogoutUrl() {
		return logoutUrl;
	}
	
	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}
	
	public Organization getOrganization() {
		return organization;
	}
	
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	
	public Set<Claim> getClaims() {
		return claims;
	}
	
	public void setClaims(Set<Claim> claims) {
		this.claims = claims;
	}
	
	public Set<Access> getAccesses() {
		return accesses;
	}
	
	public void setAccesses(Set<Access> accesses) {
		this.accesses = accesses;
	}
	
	public Set<Federation> getFederations() {
		return federations;
	}
	
	public void setFederations(Set<Federation> federations) {
		this.federations = federations;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Application)) {
			return false;
		}
		
		if (this == obj) {
			return true;
		}
		
		Application a = (Application) obj;
		return getExternalId().equals(a.getExternalId());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getExternalId());
	}

}
