package it.polito.ai.es03.services;

import it.polito.ai.es03.model.Route;

public interface RoutingService {
	
	public Route findRoute(double[] startCoordinates, double[] arriveCoordinates, int radius);
	
}
