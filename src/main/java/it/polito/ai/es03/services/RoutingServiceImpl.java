package it.polito.ai.es03.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bson.Document;
import org.hibernate.engine.jdbc.Size;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import it.polito.ai.es03.model.Route;
import it.polito.ai.es03.model.RouteBusSegment;
import it.polito.ai.es03.model.RouteBusSegmentPortion;
import it.polito.ai.es03.model.RouteSegment;
import it.polito.ai.es03.model.mongo.Edge;
import it.polito.ai.es03.model.mongo.MinPath;
import it.polito.ai.es03.model.mongo.MongoUtil;
import it.polito.ai.es03.model.postgis.BusStop;

public class RoutingServiceImpl implements RoutingService {
	
	private static final String MONGO_DB_NAME = "trasporti";
	private static final String MONGO_MIN_PATHS_COLLECTION = "MinPaths";
	
	private MongoClient mongoClient;
	private LinesService linesService;
	
	public RoutingServiceImpl(LinesService linesService){
		this.linesService = linesService;
		this.mongoClient = MongoUtil.getMongoClient();
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
		routeSegments = computeRouteSegments(bestPath);
		
		Route route = new Route();
		route.setSegments(routeSegments);
		return route;
		
	}

	private List<RouteSegment> computeRouteSegments(MinPath bestPath) {
		
		//define how many segment there are on the route
		List<RouteSegment> routeSegments = defineRouteSegments(bestPath);
		
		if(routeSegments == null)
			return null;
		
		//compute RouteBusSegmentPortions for each RouteBusSegment
		computeRouteBusSegmentsPortions(routeSegments);
		
		return routeSegments;
	}

	private void computeRouteBusSegmentsPortions(List<RouteSegment> routeSegments) {
		
		for (RouteSegment routeSegment : routeSegments) {
			if(routeSegment instanceof RouteBusSegment){
				//compute all the portions of a RouteBusSegment
				computeRouteBusSegmentPortions((RouteBusSegment) routeSegment);
			}
		}
		
	}

	private void computeRouteBusSegmentPortions(RouteBusSegment routeBusSegment) {
		
		//retrieve all the possibile lines linking two stops
		List<List<String>> listOfLinesForPortions = new ArrayList<List<String>>();
		for (int i = 0; i < routeBusSegment.getBusStops().size() -1; i++) {
			List<String> listOfLines = linesService.findLinesConnectingStops(routeBusSegment.getBusStops().get(i), routeBusSegment.getBusStops().get(i+1));
			if(listOfLines == null){
				//TODO
			}
		}
		
		//find the best combination of lines to cover the entire RouteBusSegment defining 
		//a RouteBusSegmentPortion for each different line used to cover the route bus segment
		List<String> bestCombination = computeBestCombinationOfLines(listOfLinesForPortions);
		
		//from the best combination extract all the RouteBusSegmentPortions
		int edgeIndex = 0;
		for(String lineId: bestCombination){
			Edge edge = routeBusSegment.getEdges().get(edgeIndex);
			RouteBusSegmentPortion previousRouteBusSegmentPortion = null;
			if(routeBusSegment.getRouteBusSegmentPortions().size()!=0){
				previousRouteBusSegmentPortion = routeBusSegment.getRouteBusSegmentPortions().get(routeBusSegment.getRouteBusSegmentPortions().size()-1);
			}
			
			if(previousRouteBusSegmentPortion== null || !(previousRouteBusSegmentPortion.getLineId().equals(lineId))){
				//a new RouteBusSegmentPortion starts
				RouteBusSegmentPortion routeBusSegmentPortion = new RouteBusSegmentPortion();
				routeBusSegmentPortion.setLineId(lineId);
				routeBusSegmentPortion.getEdges().add(edge);
				routeBusSegmentPortion.getBusStops().add(linesService.getBusStop(edge.getIdSource()));
				routeBusSegmentPortion.getBusStops().add(linesService.getBusStop(edge.getIdDestination()));
				routeBusSegment.getRouteBusSegmentPortions().add(routeBusSegmentPortion);
			}
			else {
				//the previous RouteBusSegmentPortion is on the same LineId so update it adding a new edge to the portion
				previousRouteBusSegmentPortion.getEdges().add(edge);
				previousRouteBusSegmentPortion.getBusStops().add(linesService.getBusStop(edge.getIdDestination()));
			}
			edgeIndex++;
		}
		
	}

	private List<String> computeBestCombinationOfLines(List<List<String>> listOfLinesForPortions) {
		//compute all the possible combination of lines to cover all the portions
		List<List<String>> combinations = new ArrayList<List<String>>();
		generateCombinations(listOfLinesForPortions, combinations, 0, null);
		
		//select the combination with less changes -> less pullmans to change
		List<String> bestCombination = selectBestCombination(combinations);
		
		return bestCombination;
	}
	
	private List<String> selectBestCombination(List<List<String>> combinations) {
		
		int bestCombinationIndex = -1;
		int minimumNumberChanges = -1;
		for (int i = 0; i < combinations.size(); i++) {
			int numberOfChanges = countChanges(combinations.get(i));
			if(i == 0){
				bestCombinationIndex = 0;
				minimumNumberChanges = numberOfChanges;
			}
			else{
				if(numberOfChanges < minimumNumberChanges){
					bestCombinationIndex = i;
					minimumNumberChanges = numberOfChanges;
				}
			}
		}
		
		return combinations.get(bestCombinationIndex);
	}

	private int countChanges(List<String> combination) {
		int changesCount = 0;
		for (int i = 0; i < combination.size()-1; i++) {
			if(!combination.get(i).equals(combination.get(i+1))){
				changesCount++;
			}
		}
		return changesCount;
	}

	void generateCombinations(List<List<String>> listOfLinesForPortions, List<List<String>> combinations, int depth, List<String> currentCombination)
	{
	    if(depth == listOfLinesForPortions.size())
	    {
	       combinations.add(currentCombination);
	       return;
	     }

	    for(int i = 0; i < listOfLinesForPortions.get(depth).size(); ++i)
	    {
	        List<String> updatedCombination = new ArrayList<String>();
	    	if(currentCombination != null){
	    		for (String string : currentCombination) {
		    		updatedCombination.add(string);
				}
	    	}
	    	String lineId = listOfLinesForPortions.get(depth).get(i);
	        updatedCombination.add(lineId);
	    	generateCombinations(listOfLinesForPortions, combinations, depth + 1, updatedCombination);
	    }
	}
	
	private List<RouteSegment> defineRouteSegments(MinPath path) {
		
		List<RouteSegment> routeSegments = new ArrayList<RouteSegment>();
		Collections.sort(path.getEdges());
		
		for (Edge edge : path.getEdges()) {
			
			RouteSegment previousRouteSegment = null;
			if(routeSegments.size()!=0){
				previousRouteSegment = routeSegments.get(routeSegments.size()-1);
			}
			
			if(edge.isMode()){
				//on foot edge
				if(previousRouteSegment == null || previousRouteSegment instanceof RouteBusSegment){
					//a new RouteSegment starts
					RouteSegment routeSegment = new RouteSegment();
					routeSegment.getEdges().add(edge);
					routeSegment.getBusStops().add(linesService.getBusStop(edge.getIdSource()));
					routeSegment.getBusStops().add(linesService.getBusStop(edge.getIdDestination()));
					routeSegments.add(routeSegment);
				}
				else {
					//the previous segment is a ROuteSegment so simply update it adding this new edge
					previousRouteSegment.getEdges().add(edge);
					previousRouteSegment.getBusStops().add(linesService.getBusStop(edge.getIdDestination()));
				}
			}
			else{
				//by bus edge
				if(previousRouteSegment == null || !(previousRouteSegment instanceof RouteBusSegment)){
					//a new RouteBusSegment starts
					RouteBusSegment routeBusSegment = new RouteBusSegment();
					routeBusSegment.getEdges().add(edge);
					routeBusSegment.getBusStops().add(linesService.getBusStop(edge.getIdSource()));
					routeBusSegment.getBusStops().add(linesService.getBusStop(edge.getIdDestination()));
					routeSegments.add(routeBusSegment);
				}
				else {
					//the previous segment is a RouteBusSegment so simply update it adding this new edge
					previousRouteSegment.getEdges().add(edge);
					previousRouteSegment.getBusStops().add(linesService.getBusStop(edge.getIdDestination()));
				}
			}
		}
		
		return routeSegments;
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
