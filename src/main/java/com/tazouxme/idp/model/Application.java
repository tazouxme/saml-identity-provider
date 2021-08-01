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

import com.tazouxme.idp.dao.query.ApplicationQueries;

@Entity
@Table(name = "tz_application")
@NamedQueries({
	@NamedQuery(name = "Application.findByUrn", query = ApplicationQueries.FIND_BY_URN),
	@NamedQuery(name = "Application.findByExternalId", query = ApplicationQueries.FIND_BY_EXTERNAL_ID)
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
	
	@Column(name = "name", length = 50, updatable = true, nullable = false)
	private String name;
	
	@Column(name = "description", length = 200, updatable = true, nullable = true)
	private String description;
	
	@Column(name = "assertion_url", length = 200, updatable = true, nullable = true)
	private String assertionUrl;

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

}
