package impl.core;

import api.core.IOrder;
import api.core.IOrderLookupCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class OrderLookupCache implements IOrderLookupCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderLookupCache.class);

    private final Map<Integer, Map<Integer, IOrder>> userOrderCache = new HashMap<>();

    public OrderLookupCache() {
        LOGGER.info("Creating OrderLookupCache");
    }

    @Override
    public synchronized void addOrder(IOrder order) {
        if (!userOrderCache.containsKey(order.getUserID())) {
            userOrderCache.put(order.getUserID(), new HashMap<>());
        }
        userOrderCache.get(order.getUserID()).put(order.getOrderID(), order);
    }

    @Override
    public synchronized void removeOrder(IOrder order) {
        Map<Integer, IOrder> userOrders = userOrderCache.get(order.getUserID());
        userOrders.remove(order.getOrderID());
        if (userOrders.isEmpty()) {
            userOrderCache.remove(order.getUserID());
        }
    }

    @Override
    public synchronized IOrder getOrder(int userID, int orderID) {
        if (userOrderCache.containsKey(userID)) {
            return userOrderCache.get(userID).get(orderID);
        }
        return null;
    }
}
