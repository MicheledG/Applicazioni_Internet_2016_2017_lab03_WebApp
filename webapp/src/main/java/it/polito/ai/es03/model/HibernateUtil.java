package it.polito.ai.es03.model;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {
	
	private static final String SERVER_ADDRESS = "192.168.99.100";
	private static final String SERVER_PORT = "5432";
	private static final String DB_NAME = "trasporti";
	private static final String DB_USERNAME = "postgres";
	private static final String DB_PASSWORD = "ai-user-password";
	private static final String DB_DRIVER = "org.postgresql.Driver";
	
	private static final String HBM2DDL_AUTO_SETTING = "validate";
	private static final String FORMAT_SQL_SETTING = "true";
	private static final String SHOW_SQL_SETTING = "true";
	private static final String CURRENT_SESSION_CONTEXT_CLASS_SETTING ="thread";
	
	private static final SessionFactory sessionFactory = buildSessionFactory();
    
	private static SessionFactory buildSessionFactory() {
    	try {
			ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
				.applySetting(Environment.DRIVER, DB_DRIVER) //for all settings
				.applySetting(Environment.URL, "jdbc:postgresql://"+SERVER_ADDRESS+":"+SERVER_PORT+"/"+DB_NAME)
				.applySetting(Environment.USER, DB_USERNAME)
				.applySetting(Environment.PASS, DB_PASSWORD)
				.applySetting(Environment.HBM2DDL_AUTO, HBM2DDL_AUTO_SETTING)
				.applySetting(Environment.FORMAT_SQL, FORMAT_SQL_SETTING)
				.applySetting(Environment.SHOW_SQL, SHOW_SQL_SETTING)
				.applySetting(Environment.CURRENT_SESSION_CONTEXT_CLASS, CURRENT_SESSION_CONTEXT_CLASS_SETTING)
				.build();
			Metadata metadata= new MetadataSources(serviceRegistry)
				.addAnnotatedClass(BusLine.class)  //for all classes
				.addAnnotatedClass(BusStop.class)
				.addAnnotatedClass(BusLineStop.class)
				.getMetadataBuilder()
				.applyImplicitNamingStrategy(ImplicitNamingStrategyJpaCompliantImpl.INSTANCE)
				.build();	
            
			return metadata.getSessionFactoryBuilder().build();
        }
        catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
	public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
