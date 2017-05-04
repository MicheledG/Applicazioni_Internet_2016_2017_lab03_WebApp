package it.polito.ai.es03.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polito.ai.es03.listeners.AppListener;
import it.polito.ai.es03.model.Route;
import it.polito.ai.es03.model.RouteBusSegment;
import it.polito.ai.es03.model.RouteBusSegmentPortion;
import it.polito.ai.es03.model.RouteSegment;
import it.polito.ai.es03.model.postgis.BusStop;
import it.polito.ai.es03.services.LinesService;
import it.polito.ai.es03.services.RoutingService;


/**
 * Servlet implementation class ComputeRouteServlet
 */
@WebServlet("/" + ComputeRouteServlet.URL)
public class ComputeRouteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public static final String URL = "computeRoute";
    public static final String REQUEST_ATTRIBUTE_ROUTE = "route";

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try{
			
			RoutingService routingService = (RoutingService) request.getServletContext().getAttribute(AppListener.CONTEXT_ATTRIBUTE_ROUTING_SERVICE);
			
			double startLat = Double.parseDouble(request.getParameter("startLat"));
			double startLng = Double.parseDouble(request.getParameter("startLng"));
			double arriveLat = Double.parseDouble(request.getParameter("arriveLat"));
			double arriveLng = Double.parseDouble(request.getParameter("arriveLng"));
			
			int radius = Integer.parseInt(request.getParameter("radius"));
			
			double[] startCoordinates = {startLat, startLng};
			double[] arriveCoordinates = {arriveLat, arriveLng};
			
			Route route = routingService.findRoute(startCoordinates, arriveCoordinates, radius);
			
			if(route != null){
				request.setAttribute(REQUEST_ATTRIBUTE_ROUTE, route);
			}
			
		} finally {
			getServletContext().getRequestDispatcher("/route.jsp").forward(request, response);
		}
	}

}
