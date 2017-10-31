package model;

import service.RadarService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Admin entity. @author MyEclipse Persistence Tools
 */

public class Company implements java.io.Serializable {

	// Fields

	private Long id;
	private String name;
	private String acronym;
	private String numeration;
	private Boolean active;
	private Set users = new HashSet(0);

	// Constructors

	/** default constructor */
	public Company() {
	}

	// Property accessors
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAcronym() {
		return acronym;
	}

	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

	public String getNumeration() {
		return numeration;
	}

	public void setNumeration(String numeration) {
		this.numeration = numeration;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Set getUsers() {
		return users;
	}

	public void setUsers(Set users) {
		this.users = users;
	}

	public static List<Company> cloneList(List<Company> list) {
		List<Company> clone = new ArrayList<>(list.size());
		for (Company item : list) clone.add(RadarService.clone(Company.class, item));
		return clone;
	}
}