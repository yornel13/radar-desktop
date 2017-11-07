package model;

import service.RadarService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Route entity. @author MyEclipse Persistence Tools
 */

public class Route implements java.io.Serializable {

	// Fields

	private Long id;
	private String name;
	private Long createDate;
	private Long lastUpdate;
	private Boolean active;
	private Set groups = new HashSet(0);
	private Set routePositions = new HashSet(0);

	// Uso local
	private boolean selected;

	// Constructors

	/** default constructor */
	public Route() {
	}

	/** minimal constructor */
	public Route(String name, Long createDate, Long lastUpdate, Boolean active) {
		this.name = name;
		this.createDate = createDate;
		this.lastUpdate = lastUpdate;
		this.active = active;
	}

	/** full constructor */
	public Route(String name, Long createDate, Long lastUpdate, Boolean active,
			Set groups, Set routePositions) {
		this.name = name;
		this.createDate = createDate;
		this.lastUpdate = lastUpdate;
		this.active = active;
		this.groups = groups;
		this.routePositions = routePositions;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Long createDate) {
		this.createDate = createDate;
	}

	public Long getLastUpdate() {
		return this.lastUpdate;
	}

	public void setLastUpdate(Long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Boolean getActive() {
		return this.active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Set getGroups() {
		return this.groups;
	}

	public void setGroups(Set groups) {
		this.groups = groups;
	}

	public Set getRoutePositions() {
		return this.routePositions;
	}

	public void setRoutePositions(Set routePositions) {
		this.routePositions = routePositions;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public static List<Route> cloneList(List<Route> list) {
		List<Route> clone = new ArrayList<>(list.size());
		for (Route item : list) clone.add(RadarService.clone(Route.class, item));
		return clone;
	}
}