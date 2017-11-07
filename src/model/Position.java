package model;

import service.RadarService;

import java.util.ArrayList;
import java.util.List;

/**
 * Position entity. @author MyEclipse Persistence Tools
 */

public class Position implements java.io.Serializable {

	// Fields

	private Long id;
	private ControlPosition controlPosition;
	private Watch watch;
	private Double latitude;
	private Double longitude;
	private Long time;
	private Long updateTime;

	// Local
	private String different;
	private int minutes;

	// Constructors

	/** default constructor */
	public Position() {
	}

	/** full constructor */
	public Position(ControlPosition controlPosition, Watch watch,
			Double latitude, Double longitude, Long time, Long updateTime) {
		this.controlPosition = controlPosition;
		this.watch = watch;
		this.latitude = latitude;
		this.longitude = longitude;
		this.time = time;
		this.updateTime = updateTime;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ControlPosition getControlPosition() {
		return this.controlPosition;
	}

	public void setControlPosition(ControlPosition controlPosition) {
		this.controlPosition = controlPosition;
	}

	public Watch getWatch() {
		return this.watch;
	}

	public void setWatch(Watch watch) {
		this.watch = watch;
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

	public Long getTime() {
		return this.time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public Long getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	public String getDifferent() {
		return different;
	}

	public void setDifferent(String different) {
		this.different = different;
	}

	public int getMinutes() {
		return minutes;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}

	public static List<Position> cloneList(List<Position> list) {
		List<Position> clone = new ArrayList<>(list.size());
		for (Position item : list) clone.add(RadarService.clone(Position.class, item));
		return clone;
	}
}