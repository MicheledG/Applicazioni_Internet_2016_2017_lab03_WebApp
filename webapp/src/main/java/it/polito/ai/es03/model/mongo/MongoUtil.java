package it.polito.ai.es03.model.mongo;

import com.mongodb.MongoClient;

public class MongoUtil {
	
	private static final String SERVER_ADDRESS = "192.168.99.100";
	private static final int SERVER_PORT = 27017;

	private static final MongoClient mongoClient = buildMongoClient();
	
	private static MongoClient buildMongoClient() {

		MongoClient mongoClient = new MongoClient(SERVER_ADDRESS, SERVER_PORT); 	
		return mongoClient;
    }
    
	public static MongoClient getMongoClient() {
        return mongoClient;
    }
			
	
	
}
