package it.polito.ai.es03.model;

import java.util.ArrayList;
import java.util.List;

import it.polito.ai.es03.model.mongo.Edge;
import it.polito.ai.es03.model.postgis.BusStop;

public class RouteSegment {

	private List<BusStop> busStops = new ArrayList<BusStop>();
	private List<Edge> edges = new ArrayList<Edge>();

	public List<BusStop> getBusStops() {
		return busStops;
	}

	public void setBusStops(List<BusStop> busStops) {
		this.busStops = busStops;
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public void setEdges(List<Edge> edges) {
		this.edges = edges;
	}
	
}
