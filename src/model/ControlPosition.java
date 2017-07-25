package model;

import jdk.nashorn.internal.ir.annotations.Ignore;

import java.util.HashSet;
import java.util.Set;

/**
 * ControlPosition entity. @author MyEclipse Persistence Tools
 */

public class ControlPosition implements java.io.Serializable {

	// Fields

	private Long id;
	private Double latitude;
	private Double longitude;
	private String placeName;
	private Boolean active;
	private Set positions = new HashSet(0);

	// Constructors

	/** default constructor */
	public ControlPosition() {
	}

	/** minimal constructor */
	public ControlPosition(Double latitude, Double longitude, String placeName) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.placeName = placeName;
	}

	/** full constructor */
	public ControlPosition(Double latitude, Double longitude, String placeName,
			Set positions) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.placeName = placeName;
		this.positions = positions;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getLatitude() {
		return this.latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return this.longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getPlaceName() {
		return this.placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	public Set getPositions() {
		return this.positions;
	}

	public void setPositions(Set positions) {
		this.positions = positions;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}


}