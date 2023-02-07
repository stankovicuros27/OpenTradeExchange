package api.core;

import api.messages.internal.util.IOrderRequestFactory;

public interface IMatchingEngine {
    public IOrderBook getOrderBook();
    public IOrderRequestFactory getOrderRequestFactory();
}
