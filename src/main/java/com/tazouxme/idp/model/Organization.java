package com.tazouxme.idp.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tazouxme.idp.dao.query.OrganizationQueries;
import com.tazouxme.idp.model.base.AbstractModel;

@Entity
@Table(name = "tz_organization", 
	uniqueConstraints = {
		@UniqueConstraint(name = "u_organization_1", columnNames = { "external_id" }),
		@UniqueConstraint(name = "u_organization_2", columnNames = { "domain" }),
		@UniqueConstraint(name = "u_organization_3", columnNames = { "code" })
	},
	indexes = {
		@Index(name = "i_organization_1", unique = true, columnList = "external_id"),
		@Index(name = "i_organization_2", unique = true, columnList = "domain"),
		@Index(name = "i_organization_3", unique = true, columnList = "code")
	})
@NamedQueries({
	@NamedQuery(name = OrganizationQueries.NQ_FIND_BY_DOMAIN, query = OrganizationQueries.FIND_BY_DOMAIN),
	@NamedQuery(name = OrganizationQueries.NQ_FIND_BY_EXTERNAL_ID, query = OrganizationQueries.FIND_BY_EXTERNAL_ID)
})
public class Organization extends AbstractModel {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Organization_generator")
	@SequenceGenerator(name = "Organization_generator", sequenceName = "Organization_sequence", allocationSize = 1)
	@Column(name = "organization_id", length = 8, updatable = false, nullable = false)
	private long id;
	
	@Column(name = "external_id", length = 16, updatable = false, nullable = false)
	private String externalId;
	
	@Column(name = "domain", length = 128, updatable = false, nullable = false)
	private String domain;
	
	@Column(name = "code", length = 16, updatable = false, nullable = false)
	private String code;
	
	@Audited
	@Column(name = "name", length = 50, updatable = true, nullable = false)
	private String name;
	
	@Audited
	@Column(name = "description", length = 200, updatable = true, nullable = true)
	private String description;
	
	@Audited
	@Column(name = "enabled", length = 1, updatable = true, nullable = false)
	private boolean enabled;
	
	@Audited
	@Column(name = "federation", length = 1, updatable = true)
	private boolean federation;
	
	@Audited
	@Lob
	@Column(name = "certificate", length = 8096, updatable = true, nullable = true)
	private byte[] certificate;
	
	@JsonIgnoreProperties("organization")
	@OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<User> users = new HashSet<>();
	
	@JsonIgnoreProperties("organization")
	@OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Application> applications = new HashSet<>();
	
	@JsonIgnoreProperties("organization")
	@OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Claim> claims = new HashSet<>();
	
	@JsonIgnoreProperties("organization")
	@OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Role> roles = new HashSet<>();

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

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isFederation() {
		return federation;
	}
	
	public void setFederation(boolean federation) {
		this.federation = federation;
	}
	
	public byte[] getCertificate() {
		return certificate;
	}
	
	public void setCertificate(byte[] certificate) {
		this.certificate = certificate;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}
	
	public Set<Application> getApplications() {
		return applications;
	}
	
	public void setApplications(Set<Application> applications) {
		this.applications = applications;
	}
	
	public Set<Claim> getClaims() {
		return claims;
	}
	
	public void setClaims(Set<Claim> claims) {
		this.claims = claims;
	}
	
	public Set<Role> getRoles() {
		return roles;
	}
	
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Organization)) {
			return false;
		}
		
		if (this == obj) {
			return true;
		}
		
		Organization a = (Organization) obj;
		return getExternalId().equals(a.getExternalId());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getExternalId());
	}

}
