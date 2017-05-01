<%@page import="java.util.ArrayList"%>
<%@page import="it.polito.ai.es03.model.RouteBusSegmentPortion"%>
<%@page import="it.polito.ai.es03.model.RouteBusSegment"%>
<%@page import="it.polito.ai.es03.model.RouteSegment"%>
<%@page import="it.polito.ai.es03.model.Route"%>
<%@page import="it.polito.ai.es03.services.RoutingService"%>
<%@page import="java.util.Collections"%>
<%@page import="it.polito.ai.es03.model.postgis.BusLineStop"%>
<%@page import="it.polito.ai.es03.listeners.AppListener"%>
<%@page import="it.polito.ai.es03.services.LinesService"%>
<%@page import="it.polito.ai.es03.model.postgis.BusStop"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%

	LinesService linesService = (LinesService) request.getServletContext().getAttribute(AppListener.CONTEXT_ATTRIBUTE_LINES_SERVICE);
	RoutingService routingService = (RoutingService) request.getServletContext().getAttribute(AppListener.CONTEXT_ATTRIBUTE_ROUTING_SERVICE);
	
	double startLat = Double.parseDouble(request.getParameter("startLat"));
	double startLng = Double.parseDouble(request.getParameter("startLng"));
	double arriveLat = Double.parseDouble(request.getParameter("arriveLat"));
	double arriveLng = Double.parseDouble(request.getParameter("arriveLng"));
	int radius = Integer.parseInt(request.getParameter("radius"));
	
	double[] startCoordinates = {startLat, startLng};
	double[] arriveCoordinates = {arriveLat, arriveLng};
	
	Route route = routingService.findRoute(startCoordinates, arriveCoordinates, radius);
	
	List<BusStop> busStops = new ArrayList<BusStop>();
	List<String> modes = new ArrayList<String>();
	
	for(RouteSegment routeSegment: route.getSegments()){
		if(routeSegment instanceof RouteBusSegment){
			RouteBusSegment routeBusSegment = (RouteBusSegment) routeSegment;
			for(RouteBusSegmentPortion routeBusSegmentPortion: routeBusSegment.getRouteBusSegmentPortions()){
				for(BusStop busStop : routeBusSegmentPortion.getBusStops()){
					busStops.add(busStop);
					modes.add(routeBusSegmentPortion.getLineId());
				}
			}
		}
		else{
			for(BusStop busStop : routeSegment.getBusStops()){
				busStops.add(busStop);
				modes.add("foot");
			}
		}
	}
	
	
%>


<!DOCTYPE html>
<html lang="en">
<head>
	<title>Bootstrap Example</title>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
 	<link rel="stylesheet" href="https://unpkg.com/leaflet@1.0.3/dist/leaflet.css" />
	<link rel="stylesheet" href="styles.css">
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
	<link rel="stylesheet" href="https://api.mapbox.com/mapbox-gl-js/v0.34.0/mapbox-gl.css"/>
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
	<script src="https://unpkg.com/leaflet@1.0.3/dist/leaflet.js"></script>
	<script src='https://api.mapbox.com/mapbox-gl-js/v0.34.0/mapbox-gl.js'></script>
</head>
<body>

<nav class="navbar navbar-default">
  <div class="container-fluid">
    <div class="navbar-header">
      <a class="navbar-brand" href="index.jsp">Bus WebApp</a>
    </div>
    <ul class="nav navbar-nav">
      <li><a href="index.jsp">Lines</a></li>
    </ul>
  </div>
</nav>
<div class="container">
  	<h1>Route</h1>
	<h2>List</h2>
	<table class="table table-striped">
	  	<thead>
	  		<tr>
	  			<th>Id</th>
	  			<th>Name</th>
	  			<th>Line</th>
	  		</tr>
	  	</thead>
	  	<tbody>
	  		<%
				int index = 0;
	  			for(BusStop busStop: busStops){
					String id = busStop.getId();
					String name = busStop.getName();
					String mode = modes.get(index);
					index++;
					
			%>
					<tr>
						<td><%=id %></td>
						<td><%=name %></td>
						<td><%=mode %></td>
					</tr>
			<% 	
				}
	  		%>
  		</tbody>
  	</table>

</div>  
</body>
</html>
