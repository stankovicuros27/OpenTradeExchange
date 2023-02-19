package tradingdatadb;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class TradingDataDBConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradingDataDBConnection.class);

    private static final int MONGODB_CLIENT_PORT = 27017;
    private static final String MONGODB_DATABASE = "tradingdatadb";
    private static final String MONGODB_TRADE_DB_ADDRESS = System.getenv("TRADE_DATA_DB_URL");


    private static TradingDataDBConnection instance = null;

    private final MongoClient mongoClient;
    private final MongoDatabase mongoDatabase;

    private TradingDataDBConnection() {
        mongoClient = new MongoClient(MONGODB_TRADE_DB_ADDRESS, MONGODB_CLIENT_PORT);
        mongoDatabase = mongoClient.getDatabase(MONGODB_DATABASE);
    }

    public static void initialize() {
        if (instance != null) {
            throw new IllegalStateException();
        }
        LOGGER.info("Initializing TradingDataDB");
        instance = new TradingDataDBConnection();
    }

    public static TradingDataDBConnection getInstance() {
        if (instance == null) {
            throw new IllegalStateException();
        }
        return instance;
    }

    public void testDB() {
        mongoClient.getDatabaseNames().forEach(System.out::println);
    }
}
