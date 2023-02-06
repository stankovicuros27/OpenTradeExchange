package api.core;

public interface IMatchingEngine {
    public IOrderBook getOrderBook();
    public IEventDataStore getEventDataStore();
}
