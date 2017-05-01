package it.polito.ai.es03.model;

import java.util.List;

public class Route {
	
	private double[] startCoordinates;
	private double[] arriveCoordinates;
	private List<RouteSegment> segments;

	public List<RouteSegment> getSegments() {
		return segments;
	}

	public void setSegments(List<RouteSegment> segments) {
		this.segments = segments;
	}

	public double[] getStartCoordinates() {
		return startCoordinates;
	}

	public void setStartCoordinates(double[] startCoordinates) {
		this.startCoordinates = startCoordinates;
	}

	public double[] getArriveCoordinates() {
		return arriveCoordinates;
	}

	public void setArriveCoordinates(double[] arriveCoordinates) {
		this.arriveCoordinates = arriveCoordinates;
	}
	
}
