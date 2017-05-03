package it.polito.ai.es03.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import it.polito.ai.es03.model.postgis.BusLine;
import it.polito.ai.es03.model.postgis.BusLineStop;
import it.polito.ai.es03.model.postgis.BusStop;
import it.polito.ai.es03.model.postgis.HibernateUtil;

public class LinesServiceImpl implements LinesService {
	
	private SessionFactory sessionFactory;
	
	public LinesServiceImpl(){
		this.sessionFactory = HibernateUtil.getSessionFactory();
	}

	public List<BusLine> getBusLines() {
		
		List<BusLine> busLines = null;
		
		//try accessing the db
		Session session = sessionFactory.getCurrentSession();
		String hql = "from BusLine";
		Query query = session.createQuery(hql);
		busLines = query.list();
			
		return busLines;
	}

	public List<BusStop> getBusStops() {
		
		List<BusStop> busStops = null;
		
		//try accessing the db
		Session session = sessionFactory.getCurrentSession();
		String hql = "from BusStop";
		Query query = session.createQuery(hql);
		busStops = query.list();
			
		return busStops;
	
	}

	public List<BusLineStop> getBusLineStops(String lineId) {
		
		BusLine busLine = null;
		List<BusLineStop> busLineStops = new ArrayList<BusLineStop>(); 
		
		//try accessing the db
		Session session = sessionFactory.getCurrentSession();
		busLine = (BusLine) session.get(BusLine.class, lineId);
		for (BusLineStop busLineStop : busLine.getLineStops()) {
			busLineStops.add(busLineStop);
		}
			
		return busLineStops;
	}

	public List<BusLineStop> getStoppingLines(String stopId) {
		
		BusStop busStop = null;
		List<BusLineStop> stoppingLines = new ArrayList<BusLineStop>(); 
		
		//try accessing the db
		Session session = sessionFactory.getCurrentSession();
		busStop = (BusStop) session.get(BusStop.class, stopId);
		for (BusLineStop stoppingLine : busStop.getStoppingLines()) {
			stoppingLines.add(stoppingLine);
		}
			
		return stoppingLines;
	}

	public BusLine getBusLine(String lineId) {
		Session session = sessionFactory.getCurrentSession();
		BusLine busLine = (BusLine) session.get(BusLine.class, lineId);
		return busLine;
	}

	public BusStop getBusStop(String stopId) {
		Session session = sessionFactory.getCurrentSession();
		BusStop busStop = (BusStop) session.get(BusStop.class, stopId);
		return busStop;
	}

	//coordinates[0] -> LATITUDE
	//coordinates[1] -> LONGITUDE
	public List<BusStop> findStopsInRadius(double[] coordinates, int radius) {
		
		Session session = sessionFactory.getCurrentSession();
		List<BusStop> busStops = new ArrayList<BusStop>();
		
		//compute distance between stops within the radius
		String textGeometry = "ST_GeographyFromText('SRID=4326;POINT("+coordinates[1]+" "+coordinates[0]+")')";
		String stringQuery = "select id "
				+ "from busstopgeo "
				+ "where ST_DWithin(position, "+textGeometry+", "+radius+");";
		List<Object> result = session.createSQLQuery(stringQuery).list();
		
		for (Object object : result) {
			String stopId = (String) object; 
			//insert into the graph
			BusStop busStop = getBusStop(stopId);
			if(busStop != null)
				busStops.add(busStop);
    	}
		
		if(busStops.size() == 0)
			return null;
		else
			return busStops;
	}

	public List<String> findLinesConnectingStops(BusStop a, BusStop b) {
		
		List<String> lines = new ArrayList<String>();
		
		for (BusLineStop stoppingLineA : a.getStoppingLines()) {
			String lineId = stoppingLineA.getBusLine().getLine();
			int thisStopSequenceNumber = stoppingLineA.getSequenceNumber();
			BusStop nextStop = getBusLineStop(lineId, thisStopSequenceNumber+1);
			if(b.equals(nextStop) && !lines.contains(lineId)){
				//the second condition is used to solve DB corruption! -> duplicate lines
				lines.add(lineId);
			}
		}
		
		if(lines.size() == 0)
			return null;
		else
			return lines;
	}

	public BusStop getBusLineStop(String lineId, int sequenceNumber) {
		List<BusLineStop> stops = getBusLine(lineId).getLineStops();
		
		Collections.sort(stops);
		
		if(sequenceNumber<1 || sequenceNumber > stops.size())
			return null;
		else
			return stops.get(sequenceNumber-1).getBusStop();
	}

	public double getDistanceFromBusStop(double[] coordinates, String stopId) {
		
		//compute distance between this stop and next stop
		String textGeometry = "ST_GeographyFromText('SRID=4326;POINT("+coordinates[1]+" "+coordinates[0]+")')";
		String stringQuery = "select ST_Distance("+textGeometry+", position) as distance "
				+ "from busstopgeo "
				+ "where id = '"+stopId+"';";
		Session session = sessionFactory.getCurrentSession();
		List<Object> result = session.createSQLQuery(stringQuery).list();    		
		double distance = -1;
		for (Object object : result) {
			distance = (Double) object;
		}
		
		return distance;

	}
	
	
}
