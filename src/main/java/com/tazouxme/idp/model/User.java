package com.tazouxme.idp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tazouxme.idp.dao.query.UserQueries;

@Entity
@Table(name = "tz_user")
@NamedQueries({
	@NamedQuery(name = "User.findByEmail", query = UserQueries.FIND_BY_EMAIL),
	@NamedQuery(name = "User.findByExternalId", query = UserQueries.FIND_BY_EXTERNAL_ID)
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
	
	@Column(name = "enabled", length = 1, updatable = true, nullable = false)
	private boolean enabled;
	
	@Column(name = "firstname", length = 100, updatable = true, nullable = true)
	private String firstname;
	
	@Column(name = "lastname", length = 100, updatable = true, nullable = true)
	private String lastname;
	
	@Column(name = "sex", length = 1, updatable = true, nullable = true)
	private Character sex;
	
	@Column(name = "birth_date", length = 16, updatable = true, nullable = true)
	private Long birthDate;
	
	@Lob
	@Column(name = "picture", updatable = true, nullable = true)
	private Byte[] picture;
	
	@Column(name = "street", length = 100, updatable = true, nullable = true)
	private String street;
	
	@Column(name = "city", length = 100, updatable = true, nullable = true)
	private String city;
	
	@Column(name = "country", length = 30, updatable = true, nullable = true)
	private String country;
	
	@Column(name = "zip_code", length = 10, updatable = true, nullable = true)
	private Long zipCode;
	
	@Column(name = "creation_date", length = 16, updatable = false, nullable = false)
	private long creationDate;

	@JsonIgnoreProperties("users")
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", foreignKey = @ForeignKey(name = "fk_tz_user_organization"))
	private Organization organization;

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

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public Character getSex() {
		return sex;
	}

	public void setSex(Character sex) {
		this.sex = sex;
	}

	public Long getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Long birthDate) {
		this.birthDate = birthDate;
	}

	public Byte[] getPicture() {
		return picture;
	}

	public void setPicture(Byte[] picture) {
		this.picture = picture;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Long getZipCode() {
		return zipCode;
	}

	public void setZipCode(Long zipCode) {
		this.zipCode = zipCode;
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

}
