package it.polito.ai.es03.services;

import java.util.List;

import it.polito.ai.es03.model.BusLine;
import it.polito.ai.es03.model.BusLineStop;
import it.polito.ai.es03.model.BusStop;

public interface LinesService {

	public List<BusLine> getBusLines();
	public List<BusStop> getBusStops();
	public List<BusLineStop> getBusLineStops(String lineId);
	public List<BusLineStop> getStoppingLines(String stopId);
	
}
