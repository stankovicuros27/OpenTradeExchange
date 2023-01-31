package api.core;

public interface IOrderFactory {
    public IOrder createOrder(int userID, Side side, int totalVolume, double price);
}
