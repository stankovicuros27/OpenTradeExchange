package api.messages.responses;

import api.messages.IMessage;

public interface ITradeResponse extends IResponse {
    public int getBuyUserID();
    public int getBuyOrderID();
    public int getSellUserID();
    public int getSellOrderID();
    public double getPrice();
    public int getVolume();
    public int getTimestamp();
}
