package it.polito.ai.es03.model;

import java.util.List;

public class RouteBusSegment extends RouteSegment {

	private List<RouteBusSegmentPortion> busSubSegments;

	public List<RouteBusSegmentPortion> getBusSubSegments() {
		return busSubSegments;
	}

	public void setBusSubSegments(List<RouteBusSegmentPortion> busSubSegments) {
		this.busSubSegments = busSubSegments;
		
	}
	
}
