<%@page import="java.util.Collections"%>
<%@page import="it.polito.ai.es03.model.postgis.BusLineStop"%>
<%@page import="it.polito.ai.es03.listeners.AppListener"%>
<%@page import="it.polito.ai.es03.services.LinesService"%>
<%@page import="it.polito.ai.es03.model.postgis.BusStop"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%

	LinesService linesService = null;
	List<BusLineStop> busLineStops = null;
	String line = null;	
	
	linesService = (LinesService) request.getServletContext()
	.getAttribute(AppListener.CONTEXT_ATTRIBUTE_LINES_SERVICE);	
		
	line = request.getParameter("line");	
	
	if(line != null){
		busLineStops = linesService.getBusLineStops(line);
	}
	
	if(busLineStops != null){
		Collections.sort(busLineStops);
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
  	<h1>GTT line <%=line%> stops</h1>
	
	<h2>Map</h2>  
	<div id='mapid'></div>
	
	<h2>List</h2>
	<table class="table table-striped">
	  	<thead>
	  		<tr>
	  			<th>Seq. Nr.</th>
	  			<th>Id</th>
	  			<th>Name</th>
	  		</tr>
	  	</thead>
	  	<tbody>
	  		<%
				for(BusLineStop busLineStop: busLineStops){
					String sequenceNumber = String.format("%1$03d", busLineStop.getSequenceNumber());
					BusStop busStop = busLineStop.getBusStop();
					String id = busStop.getId();
					String name = busStop.getName();
			%>
					<tr>
						<td><%=sequenceNumber %></td>
						<td><%=id %></td>
						<td><%=name %></td>
					</tr>
			<% 	
				}
	  		%>
  		</tbody>
  	</table>

</div>  

	<script>
		var map = L.map('mapid');//.setView([45.071228, 7.685027], 13);
		
		L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
 		    maxZoom: 18,
 		    id: 'mapbox.streets',
		    accessToken: 'pk.eyJ1IjoiY2hpZWZ6ZXBoeXIiLCJhIjoiY2oxM3djY3dhMDAxZTJxcXdseXJzNDZmeiJ9.fDhlEf0ME8ta_sl6-Hh06g'
		}).addTo(map);
				
		var latLngs = [];
		<%
		for(BusLineStop busLineStop: busLineStops){
			//for each stop of the bus line create the elements to put on the map
			//initially extract the informations
			short sequenceNumber = busLineStop.getSequenceNumber();
			BusStop busStop = busLineStop.getBusStop();
			String id = busStop.getId();
			String name = busStop.getName();
			double lat = busStop.getLat();
			double lng = busStop.getLng();
			List<BusLineStop> stoppingLines = busStop.getStoppingLines();
			
		%>
			//prepare message to show into the popup
			var markerContent<%=id%> = "id: <%=id %><br>";
			markerContent<%=id%> += "name: <%=name %><br>";
			markerContent<%=id%> += "lines:<br>";
			
			<%
			for(BusLineStop stoppingLine: stoppingLines){
				String stoppingLineName = stoppingLine.getBusLine().getLine();
			%>
				markerContent<%=id%> += "- <a href='stops.jsp?line=<%=stoppingLineName %>'><%=stoppingLineName %></a><br>";
			<%
			}
			%>
			
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
		
// 		// create a red polyline from an array of LatLng points
// 		var polyline = L.polyline(latLngs, {color: 'red'}).addTo(map);
// 		// zoom the map to the polyline
// 		map.fitBounds(polyline.getBounds());
		map.fitBounds(latLngs);		

	</script>

</body>
</html>
