package impl.core;

import api.core.*;
import api.messages.info.ILimitCollectionInfo;
import api.messages.info.ILimitInfo;
import api.messages.requests.IPlaceOrderRequest;
import api.messages.responses.IOrderStatusResponse;
import api.messages.responses.IResponse;
import api.util.ITimestampProvider;
import impl.messages.info.LimitCollectionInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class LimitCollection implements ILimitCollection {

    private final Side side;
    private final IOrderLookupCache orderLookupCache;
    private final ITimestampProvider timestampProvider;
    private final SortedMap<Double, ILimit> limits = new TreeMap<>();

    public LimitCollection(Side side, IOrderLookupCache orderLookupCache, ITimestampProvider timestampProvider) {
        this.side = side;
        this.orderLookupCache = orderLookupCache;
        this.timestampProvider = timestampProvider;
    }

    @Override
    public IOrderStatusResponse addOrder(IOrder order) {
        if (!limits.containsKey(order.getPrice())) {
            limits.put(order.getPrice(), new Limit(side, order.getPrice(), orderLookupCache, timestampProvider));
        }
        return limits.get(order.getPrice()).addOrder(order);
    }

    @Override
    public IOrderStatusResponse cancelOrder(IOrder order) {
        ILimit limit = limits.get(order.getPrice());
        IOrderStatusResponse response = limit.cancelOrder(order);
        if (limit.isEmpty()) {
            limits.remove(limit);
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
        for (ILimit limit : limits.values()) {
            limitInfos.add(limit.getLimitInfo());
            volume += limit.getVolume();
            numberOfOrders += limit.getNumberOfOrders();
        }
        return new LimitCollectionInfo(side, limitInfos, volume, numberOfOrders);
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
