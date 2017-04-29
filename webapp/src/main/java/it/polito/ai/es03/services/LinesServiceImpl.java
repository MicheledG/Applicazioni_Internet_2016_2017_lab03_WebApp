package it.polito.ai.es03.services;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import it.polito.ai.es03.model.BusLine;
import it.polito.ai.es03.model.BusLineStop;
import it.polito.ai.es03.model.BusStop;
import it.polito.ai.es03.model.HibernateUtil;

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


}
