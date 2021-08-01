package com.tazouxme.idp.model;

import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tazouxme.idp.dao.query.OrganizationQueries;

@Entity
@Table(name = "tz_organization")
@NamedQueries({
	@NamedQuery(name = "Organization.findByDomain", query = OrganizationQueries.FIND_BY_DOMAIN),
	@NamedQuery(name = "Organization.findByExternalId", query = OrganizationQueries.FIND_BY_EXTERNAL_ID)
})
public class Organization {

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
	
	@Column(name = "name", length = 50, updatable = true, nullable = false)
	private String name;
	
	@Column(name = "description", length = 200, updatable = true, nullable = true)
	private String description;
	
	@Column(name = "enabled", length = 1, updatable = true, nullable = false)
	private boolean enabled;
	
	@Lob
	@Column(name = "public_key", updatable = true, nullable = true)
	private String publicKey;
	
	@Column(name = "creation_date", length = 16, updatable = false, nullable = false)
	private long creationDate;
	
	@JsonIgnoreProperties("organization")
	@OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<User> users = new TreeSet<>((o1, o2) -> {
		return o1.getEmail().compareTo(o2.getEmail());
	});

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
	
	public String getPublicKey() {
		return publicKey;
	}
	
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

}
