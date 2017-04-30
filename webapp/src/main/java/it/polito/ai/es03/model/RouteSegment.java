package it.polito.ai.es03.model;

import java.util.List;

import it.polito.ai.es03.model.postgis.BusStop;

public class RouteSegment {

	private List<BusStop> busStops;

	public List<BusStop> getBusStops() {
		return busStops;
	}

	public void setBusStops(List<BusStop> busStops) {
		this.busStops = busStops;
	}
	
}
