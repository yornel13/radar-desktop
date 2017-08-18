package model;

import java.util.HashSet;
import java.util.Set;

/**
 * User entity. @author MyEclipse Persistence Tools
 */

public class User implements java.io.Serializable {

	// Fields

	private Long id;
	private String dni;
	private String name;
	private String lastname;
	private String password;
	private Long createDate;
	private Long lastUpdate;
	private Group group;
	private Company company;
	private Boolean active;
	private Set watchs = new HashSet(0);
	private Set routeMarkers = new HashSet(0);

	// Uso local
	private boolean selected;

	// Constructors

	/** default constructor */
	public User() {
	}

	/** minimal constructor */
	public User(String dni, String name, String lastname, String password,
				Long createDate, Long lastUpdate, Boolean active) {
		this.dni = dni;
		this.name = name;
		this.lastname = lastname;
		this.password = password;
		this.createDate = createDate;
		this.lastUpdate = lastUpdate;
		this.active = active;
	}

	/** full constructor */
	public User(Group group, String dni, String name, String lastname,
				String password, Long createDate, Long lastUpdate, Boolean active,
				Set watchs, Set routeMarkers) {
		this.group = group;
		this.dni = dni;
		this.name = name;
		this.lastname = lastname;
		this.password = password;
		this.createDate = createDate;
		this.lastUpdate = lastUpdate;
		this.active = active;
		this.watchs = watchs;
		this.routeMarkers = routeMarkers;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Group getGroup() {
		return this.group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public String getDni() {
		return this.dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastname() {
		return this.lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public Set getWatchs() {
		return this.watchs;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public void setWatchs(Set watchs) {
		this.watchs = watchs;
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