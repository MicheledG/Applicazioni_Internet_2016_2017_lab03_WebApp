package it.polito.ai.es03.model;

import java.util.ArrayList;
import java.util.List;

public class RouteBusSegment extends RouteSegment {

	private List<RouteBusSegmentPortion> routeBusSegmentPortions = new ArrayList<RouteBusSegmentPortion>();

	public List<RouteBusSegmentPortion> getRouteBusSegmentPortions() {
		return routeBusSegmentPortions;
	}

	public void setRouteBusSegmentPortions(List<RouteBusSegmentPortion> routeBusSegmentPortions) {
		this.routeBusSegmentPortions = routeBusSegmentPortions;
	}

	
}
