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
import com.tazouxme.idp.dao.query.StoreQueries;

@Entity
@Table(name = "tz_store")
@NamedQueries({
	@NamedQuery(name = StoreQueries.NQ_FIND_ALL, query = StoreQueries.FIND_ALL),
	@NamedQuery(name = StoreQueries.NQ_FIND_BY_KEY, query = StoreQueries.FIND_BY_KEY),
	@NamedQuery(name = StoreQueries.NQ_FIND_BY_KEY_AND_VERSION, query = StoreQueries.FIND_BY_KEY_AND_VERSION)
})
public class Store {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Store_generator")
	@SequenceGenerator(name = "Store_generator", sequenceName = "Store_sequence", allocationSize = 1)
	@Column(name = "store_id", length = 8, updatable = false, nullable = false)
	private long id;
	
	@Column(name = "context", length = 256, updatable = false, nullable = false)
	private String context;
	
	@Column(name = "store_key", updatable = false, nullable = false)
	private String storeKey;
	
	@Lob
	@Column(name = "store_value", updatable = false, nullable = false)
	private byte[] storeValue;
	
	@Column(name = "expiration", length = 16, updatable = false, nullable = false)
    private Long expiration;
    
	@Column(name = "version", length = 4, updatable = false, nullable = false)
    private Long version;

	@JsonIgnoreProperties("stores")
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", foreignKey = @ForeignKey(name = "fk_tz_store_organization"))
	private Organization organization;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getStoreKey() {
		return storeKey;
	}

	public void setStoreKey(String storeKey) {
		this.storeKey = storeKey;
	}

	public byte[] getStoreValue() {
		return storeValue;
	}

	public void setStoreValue(byte[] storeValue) {
		this.storeValue = storeValue;
	}

	public Long getExpiration() {
		return expiration;
	}

	public void setExpiration(Long expiration) {
		this.expiration = expiration;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
	
	public Organization getOrganization() {
		return organization;
	}
	
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

}
