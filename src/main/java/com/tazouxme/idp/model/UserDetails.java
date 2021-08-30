package com.tazouxme.idp.model;

import java.util.Objects;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "tz_user_details")
public class UserDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UserDetails_generator")
	@SequenceGenerator(name = "UserDetails_generator", sequenceName = "UserDetails_sequence", allocationSize = 1)
	@Column(name = "user_details_id", length = 8, updatable = false, nullable = false)
	private long id;
	
	@JsonIgnoreProperties("details")
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_tz_user_details"))
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", foreignKey = @ForeignKey(name = "fk_tz_user_claims"))
	private Claim claim;
	
	@Lob
	@Column(name = "claim_value", updatable = true, nullable = false)
	private String claimValue;
	
	@Column(name = "creation_date", length = 16, updatable = false, nullable = false)
	private long creationDate;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Claim getClaim() {
		return claim;
	}

	public void setClaim(Claim claim) {
		this.claim = claim;
	}

	public String getClaimValue() {
		return claimValue;
	}

	public void setClaimValue(String claimValue) {
		this.claimValue = claimValue;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof UserDetails)) {
			return false;
		}
		
		if (this == obj) {
			return true;
		}
		
		UserDetails a = (UserDetails) obj;
		return getUser().equals(a.getUser()) && getClaim().equals(a.getClaim())
				&& getClaimValue().equals(a.getClaimValue());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getClaim(), getUser(), getClaimValue());
	}

}
