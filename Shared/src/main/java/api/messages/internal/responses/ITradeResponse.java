package api.messages.internal.responses;

public interface ITradeResponse extends IResponse {
    public int getBuyUserID();
    public int getBuyOrderID();
    public int getSellUserID();
    public int getSellOrderID();
    public double getPrice();
    public int getVolume();
}
