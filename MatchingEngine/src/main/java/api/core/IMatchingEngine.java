package api.core;

import java.util.List;

public interface IMatchingEngine {
    public void registerOrderBook(IOrderBookConfiguration orderBookConfiguration);
    public boolean containsOrderBook(String bookID);
    public IOrderBook getOrderBook(String bookID);
    public List<IOrderBook> getAllOrderBooks();
}
