package model;

import service.RadarService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Watch entity. @author MyEclipse Persistence Tools
 */

public class Watch implements java.io.Serializable {

	// Fields

	private Long id;
	private User user;
	private Long startTime;
	private Long endTime;
	private Set positions = new HashSet(0);

	//Local Fields
	private List<Position> positionsList;

	// Constructors

	/** default constructor */
	public Watch() {
	}

	/** minimal constructor */
	public Watch(User user, Long startTime, Long endTime) {
		this.user = user;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	/** full constructor */
	public Watch(User user, Long startTime, Long endTime, Set positions) {
		this.user = user;
		this.startTime = startTime;
		this.endTime = endTime;
		this.positions = positions;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Long getStartTime() {
		return this.startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public Set getPositions() {
		return this.positions;
	}

	public void setPositions(Set positions) {
		this.positions = positions;
	}

	public List<Position> getPositionsList() {
		return positionsList;
	}

	public void setPositionsList(List<Position> positionsList) {
		this.positionsList = positionsList;
	}

	public static List<Watch> cloneList(List<Watch> list) {
		List<Watch> clone = new ArrayList<>(list.size());
		for (Watch item : list) clone.add(RadarService.clone(Watch.class, item));
		return clone;
	}
}