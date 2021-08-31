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
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tazouxme.idp.dao.query.ClaimQueries;
import com.tazouxme.idp.model.base.AbstractModel;

@Entity
@Table(name = "tz_claim", 
	uniqueConstraints = {
		@UniqueConstraint(name = "u_claim_1", columnNames = { "external_id" }),
		@UniqueConstraint(name = "u_claim_2", columnNames = { "uri", "organization_id" })
	},
	indexes = {
		@Index(name = "i_claim_1", unique = true, columnList = "external_id"),
		@Index(name = "i_claim_2", unique = true, columnList = "uri, organization_id")
	})
@NamedQueries({
	@NamedQuery(name = ClaimQueries.NQ_FIND_ALL, query = ClaimQueries.FIND_ALL),
	@NamedQuery(name = ClaimQueries.NQ_FIND_BY_ID, query = ClaimQueries.FIND_BY_ID),
	@NamedQuery(name = ClaimQueries.NQ_FIND_BY_URI, query = ClaimQueries.FIND_BY_URI)
})
public class Claim extends AbstractModel {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Claim_generator")
	@SequenceGenerator(name = "Claim_generator", sequenceName = "Claim_sequence", allocationSize = 1)
	@Column(name = "claim_id", length = 8, updatable = false, nullable = false)
	private long id;
	
	@Column(name = "external_id", length = 16, updatable = false, nullable = false)
	private String externalId;
	
	@Column(name = "uri", length = 64, updatable = false, nullable = false)
	private String uri;
	
	@Column(name = "name", length = 16, updatable = false, nullable = false)
	private String name;
	
	@Column(name = "description", length = 128, updatable = true, nullable = false)
	private String description;

	@JsonIgnoreProperties("claims")
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", foreignKey = @ForeignKey(name = "fk_tz_claim_organization"))
	private Organization organization;
	
	@JsonIgnoreProperties("claims")
	@ManyToMany(mappedBy = "claims")
	private Set<Application> applications = new HashSet<>();
	
	@JsonIgnoreProperties("claim")
	@OneToMany(mappedBy = "claim", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<UserDetails> details = new HashSet<>();

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

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
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
	
	public Organization getOrganization() {
		return organization;
	}
	
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	
	public Set<Application> getApplications() {
		return applications;
	}
	
	public void setApplications(Set<Application> applications) {
		this.applications = applications;
	}
	
	public Set<UserDetails> getDetails() {
		return details;
	}
	
	public void setDetails(Set<UserDetails> details) {
		this.details = details;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Claim)) {
			return false;
		}
		
		if (this == obj) {
			return true;
		}
		
		Claim a = (Claim) obj;
		return getExternalId().equals(a.getExternalId());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getExternalId());
	}

}
