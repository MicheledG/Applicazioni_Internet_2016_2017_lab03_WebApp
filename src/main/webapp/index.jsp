<%@page import="java.util.Collections"%>
<%@page import="java.util.Collection"%>
<%@page import="it.polito.ai.es03.model.postgis.BusLine"%>
<%@page import="java.util.List"%>
<%@page import="it.polito.ai.es03.services.LinesService"%>
<%@page import="it.polito.ai.es03.listeners.AppListener"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%

	List<BusLine> busLineList = null;

	LinesService linesService = (LinesService) request.getServletContext()
	.getAttribute(AppListener.CONTEXT_ATTRIBUTE_LINES_SERVICE);
	
	if(linesService != null){
		busLineList = linesService.getBusLines();
	}
	
	Collections.sort(busLineList);
	
%>


<!DOCTYPE html>
<html lang="en">
<head>
  <title>Bus WebApp</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>
<body>

<nav class="navbar navbar-default">
  <div class="container-fluid">
    <div class="navbar-header">
      <a class="navbar-brand" href="index.jsp">Bus WebApp</a>
    </div>
    <ul class="nav navbar-nav">
      	<li class="active"><a href="index.jsp">Lines</a></li>
   		<li><a href="route.jsp">Route</a></li>
    </ul>
  </div>
</nav>
  
<div class="container">
  <h1>GTT Lines</h1>
  <table class="table table-striped">
  	<thead>
  		<tr>
  			<th>Name</th>
  			<th>Description</th>
  		</tr>
  	</thead>
  	<tbody>
  		<%
			for(BusLine busLine: busLineList){
			String name = busLine.getLine();
			String description = busLine.getDescription();
			String query = "?line="+name;
		%>
			<tr>
				<td>
					<a href="stops.jsp<%=query%>"><%=name %></a>
				</td>
				<td><%=description %></td>
			</tr>
		<% 	
			}
  		%>
  	</tbody>
  </table>
</div>

</body>
</html>
