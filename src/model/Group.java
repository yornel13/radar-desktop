package model;

import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Set;

/**
 * Group entity. @author MyEclipse Persistence Tools
 */

public class Group implements java.io.Serializable {

	// Fields

	private Long id;
	private Route route;
	private String name;
	private Long createDate;
	private Long lastUpdate;
	private Boolean active;
	private Set users = new HashSet(0);

	// Constructors

	/** default constructor */
	public Group() {
	}

	/** minimal constructor */
	public Group(String name, Long createDate, Long lastUpdate, Boolean active) {
		this.name = name;
		this.createDate = createDate;
		this.lastUpdate = lastUpdate;
		this.active = active;
	}

	/** full constructor */
	public Group(Route route, String name, Long createDate, Long lastUpdate,
			Boolean active, Set users) {
		this.route = route;
		this.name = name;
		this.createDate = createDate;
		this.lastUpdate = lastUpdate;
		this.active = active;
		this.users = users;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Route getRoute() {
		return this.route;
	}

	public void setRoute(Route route) {
		this.route = route;
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

	public Set getUsers() {
		return this.users;
	}

	public void setUsers(Set users) {
		this.users = users;
	}
}