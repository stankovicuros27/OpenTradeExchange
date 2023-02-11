package api.messages.trading;

import java.io.Serializable;

public interface IMicroFIXMessage extends Serializable {
    public String getBookID();
    public int getUserID();
    public int getOrderID();
    public double getPrice();
    public MicroFIXSide getSide();
    public int getVolume();
    public int getTimestamp();
    public int getExternalTimestamp();
    public MicroFIXMessageType getExternalMessageType();
}
