package api.core;

import api.messages.util.IOrderRequestFactory;

public interface IMatchingEngine {
    public IOrderBook getOrderBook();
    public IOrderRequestFactory getOrderRequestFactory();
    public IEventDataStore getEventDataStore();
}
