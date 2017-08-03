package model;

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
	private Set routePositions = new HashSet(0);
	private Set routeMarkers = new HashSet(0);

	// Uso local
	private boolean selected;

	// Constructors

	/** default constructor */
	public ControlPosition() {
	}

	/** minimal constructor */
	public ControlPosition(Double latitude, Double longitude, String placeName,
			Boolean active) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.placeName = placeName;
		this.active = active;
	}

	/** full constructor */
	public ControlPosition(Double latitude, Double longitude, String placeName,
						   Boolean active, Set positions, Set routePositions, Set routeMarkers) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.placeName = placeName;
		this.active = active;
		this.positions = positions;
		this.routePositions = routePositions;
		this.routeMarkers = routeMarkers;
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

	public Boolean getActive() {
		return this.active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Set getPositions() {
		return this.positions;
	}

	public void setPositions(Set positions) {
		this.positions = positions;
	}

	public Set getRoutePositions() {
		return this.routePositions;
	}

	public void setRoutePositions(Set routePositions) {
		this.routePositions = routePositions;
	}

	public Set getRouteMarkers() {
		return this.routeMarkers;
	}

	public void setRouteMarkers(Set routeMarkers) {
		this.routeMarkers = routeMarkers;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}