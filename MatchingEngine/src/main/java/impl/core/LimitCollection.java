package impl.core;

import api.core.*;
import api.messages.info.ILimitCollectionInfo;
import api.messages.info.ILimitInfo;
import api.messages.requests.IPlaceOrderRequest;
import api.messages.responses.IOrderStatusResponse;
import api.messages.responses.IResponse;
import api.core.Side;
import api.time.ITimestampProvider;
import impl.messages.info.LimitCollectionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class LimitCollection implements ILimitCollection {

    private static final Logger LOGGER = LoggerFactory.getLogger(LimitCollection.class);

    private final String bookID;
    private final Side side;
    private final IOrderLookupCache orderLookupCache;
    private final ITimestampProvider timestampProvider;
    private final SortedMap<Double, ILimit> limits = new TreeMap<>();

    public LimitCollection(String bookID, Side side, IOrderLookupCache orderLookupCache, ITimestampProvider timestampProvider) {
        LOGGER.info("Creating LimitCollection (" + side + ")");
        this.bookID = bookID;
        this.side = side;
        this.orderLookupCache = orderLookupCache;
        this.timestampProvider = timestampProvider;
    }

    @Override
    public IOrderStatusResponse addOrder(IOrder order) {
        if (!limits.containsKey(order.getPrice())) {
            limits.put(order.getPrice(), new Limit(bookID, side, order.getPrice(), orderLookupCache, timestampProvider));
        }
        return limits.get(order.getPrice()).addOrder(order);
    }

    @Override
    public IOrderStatusResponse cancelOrder(IOrder order) {
        ILimit limit = limits.get(order.getPrice());
        IOrderStatusResponse response = limit.cancelOrder(order);
        if (limit.isEmpty()) {
            limits.remove(limit.getPrice());
        }
        return response;
    }

    @Override
    public List<IResponse> matchOrderRequest(IPlaceOrderRequest orderRequest) {
        List<IResponse> responses = new ArrayList<>();
        while(!orderRequest.isMatched() && !limits.isEmpty() && canMatchOrders(orderRequest)) {
            ILimit bestLimit = getBestPriceLevel();
            responses.addAll(bestLimit.matchOrderRequest(orderRequest));
            if (bestLimit.isEmpty()) {
                limits.remove(bestLimit.getPrice());
            }
        }
        return responses;
    }

    @Override
    public boolean containsLimit(double price) {
        return limits.containsKey(price);
    }

    @Override
    public Side getSide() {
        return side;
    }

    @Override
    public ILimitCollectionInfo getInfo() {
        List<ILimitInfo> limitInfos = new ArrayList<>();
        int volume = 0;
        int numberOfOrders = 0;
        double bestPrice = -1;
        for (ILimit limit : limits.values()) {
            if (bestPrice == -1) {
                bestPrice = limit.getPrice();
            } else {
                bestPrice = side == Side.BUY ? Math.max(bestPrice, limit.getPrice()) : Math.min(bestPrice, limit.getPrice());
            }
            limitInfos.add(limit.getLimitInfo());
            volume += limit.getVolume();
            numberOfOrders += limit.getNumberOfOrders();
        }
        int timestamp = timestampProvider.getTimestampNow();
        return new LimitCollectionInfo(bookID, side, limitInfos, volume, numberOfOrders, bestPrice, timestamp);
    }

    private boolean canMatchOrders(IPlaceOrderRequest orderRequest) {
        if (side == Side.BUY) {
            return limits.get(limits.lastKey()).getPrice() >= orderRequest.getPrice();
        } else {
            return limits.get(limits.firstKey()).getPrice() <= orderRequest.getPrice();
        }
    }

    private ILimit getBestPriceLevel() {
        return side == Side.BUY ? limits.get(limits.lastKey()) : limits.get(limits.firstKey());
    }

}
