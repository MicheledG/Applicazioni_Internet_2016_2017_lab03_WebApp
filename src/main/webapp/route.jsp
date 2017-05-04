	<%@page import="it.polito.ai.es03.controllers.ComputeRouteServlet"%>
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
		
		Route route = (Route) request.getAttribute(ComputeRouteServlet.REQUEST_ATTRIBUTE_ROUTE);
		List<BusStop> busStops = new ArrayList<BusStop>();
		List<String> modes = new ArrayList<String>();
		if(route != null){
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
	      <li class="active"><a href="route.jsp">Route</a></li>
	    </ul>
	  </div>
	</nav>
	<div class="container">
	  	<h1>Route</h1>
		
		<h2>Map</h2>  
		<div id='mapid'></div>
		
		
		<% 
		if(route == null){
		%>
		<h3>Select start and arrive points on the map</h3>
		<button id="computeRouteButton" type="button" class="btn btn-default"  onclick="requestRouteComputation()" disabled> Compute Route </button>
		<%
		}
		else{
		%>
		<h2>List</h2>
		<a href="route.jsp" class="btn btn-default" >New Route</a>
		<table class="table table-striped">
		  	<thead>
		  		<tr>
		  			<th>Sequence</th>
		  			<th>Name</th>
		  			<th>Line</th>
		  		</tr>
		  	</thead>
		  	<tbody>
		  		<tr>
					<td>START</td>
					<td>start point</td>
					<td>foot</td>
				</tr>
		  		<%
					int index = 0;
		  			for(BusStop busStop: busStops){
						String id = busStop.getId();
						String name = busStop.getName();
						String mode = modes.get(index);
						index++;
						
				%>
						<tr>
							<td><%=index %></td>
							<td><%=name %></td>
							<td>
							<%
							if(mode.equals("foot")){	
							%>	
								foot
							<%
							}
							else{
							%>
								<a href="stops.jsp?line=<%=mode%>"><%=mode %></a>	
							<%
							}
							%>
							</td>
						</tr>
				<% 	
					}
		  		%>
		  		<tr>
					<td>ARRIVE</td>
					<td>arrive point</td>
					<td>foot</td>
				</tr>
	  		</tbody>
	  	</table>
		<%
		}
		%>
	
	</div>  
	</body>
	
		<script>
			var map = L.map('mapid').setView([45.071228, 7.685027], 13);
			L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
	 		    maxZoom: 18,
	 		    id: 'mapbox.streets',
			    accessToken: 'pk.eyJ1IjoiY2hpZWZ6ZXBoeXIiLCJhIjoiY2oxM3djY3dhMDAxZTJxcXdseXJzNDZmeiJ9.fDhlEf0ME8ta_sl6-Hh06g'
			}).addTo(map);
			
			<%
			if(route==null){
			%>
			
			var startAndArriveMarkers = [];
			var startAndArriveCoordinates = [];
			
			function onMapClick(e) {
			    if(startAndArriveCoordinates.length == 0){
					var startMarkerContent = "Start Point<br>";
					startMarkerContent += "latitude: "+e.latlng.lat+"<br>";
					startMarkerContent += "longitude: "+e.latlng.lng+"<br>";
			    	var startMarker = L.marker([e.latlng.lat, e.latlng.lng]).addTo(map);
					startMarker.bindPopup(startMarkerContent);
			        startMarker.openPopup();
			        startAndArriveMarkers.push(startMarkerContent);
			        startAndArriveCoordinates.push(e.latlng);
			    }
			    else if(startAndArriveCoordinates.length == 1){
			    	var arriveMarkerContent = "Arrive Point<br>";
					arriveMarkerContent += "latitude: "+e.latlng.lat+"<br>";
					arriveMarkerContent += "longitude: "+e.latlng.lng+"<br>";
			    	var arriveMarker = L.marker([e.latlng.lat, e.latlng.lng]).addTo(map);
					arriveMarker.bindPopup(arriveMarkerContent);
			        arriveMarker.openPopup();
			        startAndArriveMarkers.push(arriveMarkerContent);
			        startAndArriveCoordinates.push(e.latlng);
			        document.getElementById("computeRouteButton").disabled = false;
			    }
			}

			map.on('click', onMapClick);
		
			function requestRouteComputation(){
				var startLat = startAndArriveCoordinates[0].lat;
		    	var startLng = startAndArriveCoordinates[0].lng;
		    	var arriveLat = startAndArriveCoordinates[1].lat;
		    	var arriveLng = startAndArriveCoordinates[1].lng;
		    	window.location.href = "computeRoute?startLat="+startLat+"&startLng="+startLng+"&arriveLat="+arriveLat+"&arriveLng="+arriveLng+"&radius=250";
			}
			
			<%
			}
			else{
			%>
				var latLngs = [];
				//prepare startMarker
				var startLat = parseFloat(<%=route.getStartCoordinates()[0] %>); 
				var startLng = parseFloat(<%=route.getStartCoordinates()[1] %>);
				var startMarkerContent = "Start Point<br>";
				//startMarkerContent += "latitude: "+startLat+"<br>";
				//startMarkerContent += "longitude: "+startLng+"<br>";
		    	var startMarker = L.marker([startLat, startLng]).addTo(map);
				startMarker.bindPopup(startMarkerContent);
		        latLngs.push([startLat, startLng]);
		        //prepare arriveMarker
				var arriveLat = parseFloat(<%=route.getArriveCoordinates()[0] %>); 
				var arriveLng = parseFloat(<%=route.getArriveCoordinates()[1] %>);
				var arriveMarkerContent = "Arrive Point<br>";
				//arriveMarkerContent += "latitude: "+arriveLat+"<br>";
				//arriveMarkerContent += "longitude: "+arriveLng+"<br>";
		    	var arriveMarker = L.marker([arriveLat, arriveLng]).addTo(map);
				arriveMarker.bindPopup(arriveMarkerContent);
		        latLngs.push([arriveLat, arriveLng]);
				
				<%
				int i = 0;
				for(BusStop busStop: busStops){
					//for each stop of the route add a marker
					//initially extract the informations
					String id = busStop.getId();
					String name = busStop.getName();
					double lat = busStop.getLat();
					double lng = busStop.getLng();
					String line = modes.get(i);
					i++;
					
				%>
					//prepare message to show into the popup
					var markerContent<%=id%> = "id: <%=id %><br>";
					markerContent<%=id%> += "name: <%=name %><br>";
					markerContent<%=id%> += "line: <a href='stops.jsp?line=<%=line%>'><%=line%></a>";
					var marker<%=id %> = L.marker([<%=lat %>, <%=lng %>]).addTo(map);
					//link the marker to the popup message
					marker<%=id %>.bindPopup(markerContent<%=id%>);
					function onMarker<%=id %>Click(e){
						marker<%=id %>.openPopup();
					}
					marker<%=id %>.on('click', onMarker<%=id %>Click);
				
					latLngs.push([<%=lat %>, <%=lng %>]);
				<%
				}
				%>
				map.fitBounds(latLngs);
				startMarker.openPopup();
			<%
			}
			%>
		</script>
	</html>
