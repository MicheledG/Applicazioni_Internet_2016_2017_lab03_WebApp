package it.polito.ai.es03.services;

import java.util.List;

import it.polito.ai.es03.model.postgis.BusLine;
import it.polito.ai.es03.model.postgis.BusLineStop;
import it.polito.ai.es03.model.postgis.BusStop;

public interface LinesService {

	public BusLine getBusLine(String lineId);
	public List<BusLine> getBusLines();
	public BusStop getBusStop(String stopId);
	public List<BusStop> getBusStops();
	public BusStop getBusLineStop(String lineId, int sequenceNumber);
	public List<BusLineStop> getBusLineStops(String lineId);
	public List<BusLineStop> getStoppingLines(String stopId);
	//coordinates[0] -> LATITUDE
	//coordinates[1] -> LONGITUDE
	public List<BusStop> findStopsInRadius(double[] coordinates, int radius);
	public List<String> findLinesConnectingStops(BusStop a, BusStop b);
	
}
