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
import com.tazouxme.idp.dao.query.RoleQueries;
import com.tazouxme.idp.model.base.AbstractModel;

@Entity
@Table(name = "tz_role", 
	uniqueConstraints = {
		@UniqueConstraint(name = "u_role_1", columnNames = { "external_id" }),
		@UniqueConstraint(name = "u_role_2", columnNames = { "uri", "organization_id" })
	},
	indexes = {
		@Index(name = "i_role_1", unique = true, columnList = "external_id"),
		@Index(name = "i_role_2", unique = true, columnList = "uri, organization_id")
	})
@NamedQueries({
	@NamedQuery(name = RoleQueries.NQ_FIND_ALL, query = RoleQueries.FIND_ALL),
	@NamedQuery(name = RoleQueries.NQ_FIND_BY_ID, query = RoleQueries.FIND_BY_ID),
	@NamedQuery(name = RoleQueries.NQ_FIND_BY_URI, query = RoleQueries.FIND_BY_URI)
})
public class Role extends AbstractModel {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Role_generator")
	@SequenceGenerator(name = "Role_generator", sequenceName = "Role_sequence", allocationSize = 1)
	@Column(name = "role_id", length = 8, updatable = false, nullable = false)
	private long id;
	
	@Column(name = "external_id", length = 16, updatable = false, nullable = false)
	private String externalId;
	
	@Column(name = "uri", length = 64, updatable = false, nullable = false)
	private String uri;
	
	@Column(name = "name", length = 16, updatable = false, nullable = false)
	private String name;

	@JsonIgnoreProperties("claims")
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", foreignKey = @ForeignKey(name = "fk_tz_role_organization"))
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
	
	public Organization getOrganization() {
		return organization;
	}
	
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Role)) {
			return false;
		}
		
		if (this == obj) {
			return true;
		}
		
		Role a = (Role) obj;
		return getExternalId().equals(a.getExternalId());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getExternalId());
	}

}
