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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tazouxme.idp.dao.query.UserQueries;

@Entity
@Table(name = "tz_user")
@NamedQueries({
	@NamedQuery(name = UserQueries.NQ_FIND_BY_EMAIL, query = UserQueries.FIND_BY_EMAIL),
	@NamedQuery(name = UserQueries.NQ_FIND_BY_EXTERNAL_ID, query = UserQueries.FIND_BY_EXTERNAL_ID)
})
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "User_generator")
	@SequenceGenerator(name = "User_generator", sequenceName = "User_sequence", allocationSize = 1)
	@Column(name = "user_id", length = 8, updatable = false, nullable = false)
	private long id;
	
	@Column(name = "external_id", length = 16, updatable = false, nullable = false)
	private String externalId;
	
	@Column(name = "username", length = 128, updatable = false, nullable = false)
	private String username;
	
	@Column(name = "email", length = 128, updatable = false, nullable = false)
	private String email;
	
	@Column(name = "password", length = 250, updatable = true, nullable = false)
	private String password;
	
	@Column(name = "administrator", length = 8, updatable = true, nullable = false)
	private boolean administrator;
	
	@Column(name = "enabled", length = 1, updatable = true, nullable = false)
	private boolean enabled;
	
	@Column(name = "creation_date", length = 16, updatable = false, nullable = false)
	private long creationDate;

	@JsonIgnoreProperties("users")
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", foreignKey = @ForeignKey(name = "fk_tz_user_organization"))
	private Organization organization;
	
	@JsonIgnoreProperties("user")
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<UserDetails> details = new HashSet<>();
	
	@JsonIgnoreProperties("user")
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Access> accesses = new HashSet<>();

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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean isAdministrator() {
		return administrator;
	}
	
	public void setAdministrator(boolean administrator) {
		this.administrator = administrator;
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
	
	public Set<UserDetails> getDetails() {
		return details;
	}
	
	public void setDetails(Set<UserDetails> details) {
		this.details = details;
	}
	
	public Set<Access> getAccesses() {
		return accesses;
	}
	
	public void setAccesses(Set<Access> accesses) {
		this.accesses = accesses;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof User)) {
			return false;
		}
		
		if (this == obj) {
			return true;
		}
		
		User a = (User) obj;
		return getExternalId().equals(a.getExternalId());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getExternalId());
	}

}
