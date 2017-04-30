package it.polito.ai.es03.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bson.Document;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import it.polito.ai.es03.model.Route;
import it.polito.ai.es03.model.RouteSegment;
import it.polito.ai.es03.model.mongo.MinPath;
import it.polito.ai.es03.model.postgis.BusStop;

public class RoutingServiceImpl implements RoutingService {
	
	private static final String MONGO_DB_NAME = "trasporti";
	private static final String MONGO_MIN_PATHS_COLLECTION = "MinPaths";
	
	private MongoClient mongoClient;
	private LinesService linesService;
	
	public RoutingServiceImpl(LinesService linesService){
		this.linesService = linesService;
	}
	
	public Route findRoute(double[] startCoordinates, double[] arriveCoordinates, int radius) {

		//find the minimum paths that connect starting point to arrive point
		List<MinPath> minPaths = findMinimumPaths(startCoordinates, arriveCoordinates, radius);
		
		if(minPaths == null){
			//no path between start and arrive point
			return null;
		}
		
		//select the cheaper minimum path
		MinPath bestPath = selectBestPath(minPaths);
		
		if(bestPath == null){
			//no path between start and arrive point
			return null;
		}
		
		//determine the segments of the route based on the best path
		List<RouteSegment> routeSegments;
		try{
			routeSegments = computeRouteSegments(bestPath);
		} catch (Exception e) {
			return null;
		}
		
		Route route = new Route();
		route.setSegments(routeSegments);
		return route;
		
	}

	private List<RouteSegment> computeRouteSegments(MinPath bestPath) {
		// TODO Auto-generated method stub
		return null;
	}

	private MinPath selectBestPath(List<MinPath> minPaths) {
		Collections.sort(minPaths);
		return minPaths.get(0);
	}

	private List<MinPath> findMinimumPaths(double[] startCoordinates, double[] arriveCoordinates, int radius) {
		
		//find bus stop within the radius of the start point
		List<BusStop> startStops = linesService.findStopsInRadius(startCoordinates, radius);
		if(startStops == null){
			return null;
		}
		List<BusStop> arriveStops = linesService.findStopsInRadius(arriveCoordinates, radius);
		if(arriveStops == null){
			return null;
		}
		
		//find the minimum path linking the start and arrive stops
		List<MinPath> minPaths = new ArrayList<MinPath>();
		for (BusStop startStop : startStops) {
			for (BusStop arriveStop : arriveStops) {
				MinPath minPath = findMinimumPath(startStop, arriveStop);
				if(minPath != null){
					minPaths.add(minPath);
				}
			}
		}
		
		if(minPaths.size() == 0)
			return null;
		else
			return minPaths;
	}

	private MinPath findMinimumPath(BusStop startStop, BusStop arriveStop) {
		MongoDatabase connection = this.mongoClient.getDatabase(MONGO_DB_NAME);
		MongoCollection<Document> minPathsCollection = connection.getCollection(MONGO_MIN_PATHS_COLLECTION);
		
		//find the minimum path starting from start point and arriving at arrive point
		MongoCursor<Document> cursor = minPathsCollection.find(
				Filters.and(
						Filters.eq("idSource", startStop.getId()),
						Filters.eq("idDestination", arriveStop.getId())
						)
				).iterator();
		
		MinPath minPath = null;
		try {
			if(cursor.hasNext()){
				minPath = new Gson().fromJson(cursor.next().toJson().toString(), MinPath.class);
			}
		} finally {
			cursor.close();
		}
		
		return minPath;
	}

}
