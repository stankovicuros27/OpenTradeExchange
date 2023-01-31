package api.core;

public interface IOrderLookupCache {
    public void addOrder(IOrder order);
    public void removeOrder(IOrder order);
    public IOrder getOrder(int userID, int orderID);
}
