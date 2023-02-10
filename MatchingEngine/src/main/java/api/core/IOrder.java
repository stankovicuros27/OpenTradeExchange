package api.core;

public interface IOrder {
    public String getBookID();
    public int getUserID();
    public int getOrderID();
    public double getPrice();
    public int getTimestamp();
    public Side getSide();
    public int getTotalVolume();
    public int getUnfilledVolume();
    public void fillVolume(int volume);
    public boolean isClosed();
}
