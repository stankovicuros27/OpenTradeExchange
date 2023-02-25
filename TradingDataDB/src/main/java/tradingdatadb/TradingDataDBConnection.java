package tradingdatadb;

import api.messages.trading.response.IMicroFIXResponse;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class TradingDataDBConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradingDataDBConnection.class);

    private static final int MONGODB_CLIENT_PORT = 27017;
    private static final String MONGODB_DATABASE = "tradingdatadb";
    private static final String MONGODB_TRADE_DB_ADDRESS = System.getenv("TRADE_DATA_DB_URL");
    private static final String ORDER_COLLECTION = "orderCollection";

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

    public List<Document> getAllOrders(int userID) {
        MongoCollection<Document> orderCollection = mongoDatabase.getCollection(ORDER_COLLECTION);
        FindIterable<Document> orders = orderCollection.find(Filters.eq("userID", userID));
        Iterator iterator = orders.iterator();
        List<Document> orderDocuments = new ArrayList<>();
        while(iterator.hasNext()) {
            orderDocuments.add((Document) iterator.next());
        }
        return orderDocuments;
    }

    public void insertPlaceOrder(IMicroFIXResponse placeOrderAckResponse) {
        MongoCollection<Document> orderCollection = mongoDatabase.getCollection(ORDER_COLLECTION);
        Document orderDocument = new Document();
        orderDocument.append("bookID", placeOrderAckResponse.getBookID());
        orderDocument.append("userID", placeOrderAckResponse.getUserID());
        orderDocument.append("orderID", placeOrderAckResponse.getOrderID());
        orderDocument.append("price", placeOrderAckResponse.getPrice());
        orderDocument.append("side", placeOrderAckResponse.getSide().toString());
        orderDocument.append("volume", placeOrderAckResponse.getVolume());
        orderDocument.append("filledVolume", 0);
        orderDocument.append("timestamp", placeOrderAckResponse.getTimestamp());
        orderDocument.append("status", "active");
        orderCollection.insertOne(orderDocument);
    }

    public void insertTrade(IMicroFIXResponse tradeResponse) {
        String bookID = tradeResponse.getBookID();
        int userID = tradeResponse.getUserID();
        int orderID = tradeResponse.getOrderID();
        int filledVolume = tradeResponse.getVolume();
        updateOrderVolume(bookID, userID, orderID, filledVolume);
    }

    public void insertCancelOrder(IMicroFIXResponse tradeResponse) {
        String bookID = tradeResponse.getBookID();
        int userID = tradeResponse.getUserID();
        int orderID = tradeResponse.getOrderID();
        updateOrderStatus(bookID, userID, orderID, "closed");
    }

    private void updateOrderVolume(String bookID, int userID, int orderID, int filledVolume) {
        MongoCollection<Document> orderCollection = mongoDatabase.getCollection(ORDER_COLLECTION);
        Bson filters = Filters.and(Filters.eq("bookID", bookID), Filters.eq("userID", userID), Filters.eq("orderID", orderID));
        orderCollection.updateOne(filters, Updates.inc("filledVolume", filledVolume));
        Document order = orderCollection.find(filters).iterator().next();
        if (Objects.equals(order.getInteger("filledVolume"), order.getInteger("volume"))) {
            updateOrderStatus(bookID, userID, orderID, "closed");
        }
    }

    private void updateOrderStatus(String bookID, int userID, int orderID, String orderStatus) {
        MongoCollection<Document> orderCollection = mongoDatabase.getCollection(ORDER_COLLECTION);
        Bson filters = Filters.and(Filters.eq("bookID", bookID), Filters.eq("userID", userID), Filters.eq("orderID", orderID));
        orderCollection.updateOne(filters, Updates.set("status", orderStatus));
    }
}
