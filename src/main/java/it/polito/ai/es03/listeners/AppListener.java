package it.polito.ai.es03.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.hibernate.SessionFactory;

import it.polito.ai.es03.model.Route;
import it.polito.ai.es03.model.postgis.HibernateUtil;
import it.polito.ai.es03.services.LinesService;
import it.polito.ai.es03.services.LinesServiceImpl;
import it.polito.ai.es03.services.RoutingService;
import it.polito.ai.es03.services.RoutingServiceImpl;

/**
 * Application Lifecycle Listener implementation class AppListener
 *
 */
@WebListener
public class AppListener implements ServletContextListener {

	public static final String CONTEXT_ATTRIBUTE_LINES_SERVICE= "linesService";
	public static final String CONTEXT_ATTRIBUTE_ROUTING_SERVICE= "routingService";
	
	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0)  { 
         // TODO Auto-generated method stub
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0)  { 
    	LinesService linesService = new LinesServiceImpl();
    	RoutingService routingService = new RoutingServiceImpl(linesService);
		arg0.getServletContext().setAttribute(CONTEXT_ATTRIBUTE_LINES_SERVICE, linesService);
		arg0.getServletContext().setAttribute(CONTEXT_ATTRIBUTE_ROUTING_SERVICE, routingService);
    }
	
}
