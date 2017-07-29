package model;

import org.hibernate.Query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * User entity. @author MyEclipse Persistence Tools
 */

public class User extends ArrayList<User> implements java.io.Serializable {

	// Fields

	private Long id;
	private String dni;
	private String name;
	private String lastname;
	private String password;
	private Long create;
	private Long update;
	private Boolean active;
	private Set watchs = new HashSet(0);

	// Constructors

	/** default constructor */
	public User() {
	}

	/** minimal constructor */
	public User(String name, String lastname) {
		this.name = name;
		this.lastname = lastname;
	}

	/** full constructor */
	public User(String name, String lastname, Set watchs) {
		this.name = name;
		this.lastname = lastname;
		this.watchs = watchs;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDni() {
		return dni;
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
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getCreate() {
		return create;
	}

	public void setCreate(Long create) {
		this.create = create;
	}

	public Long getUpdate() {
		return update;
	}

	public void setUpdate(Long update) {
		this.update = update;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Set getWatchs() {
		return this.watchs;
	}

	public void setWatchs(Set watchs) {
		this.watchs = watchs;
	}



}