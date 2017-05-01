package it.polito.ai.es03.model;

import java.util.List;

public class Route {
	
	private List<RouteSegment> segments;

	public List<RouteSegment> getSegments() {
		return segments;
	}

	public void setSegments(List<RouteSegment> segments) {
		this.segments = segments;
	}
	
}
