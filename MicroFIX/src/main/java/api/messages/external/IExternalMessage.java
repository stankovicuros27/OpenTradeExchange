package api.messages.external;

import java.io.Serializable;

public interface IExternalMessage extends Serializable {
    public String getBookID();
    public int getUserID();
    public int getOrderID();
    public double getPrice();
    public ExternalSide getSide();
    public int getVolume();
    public int getTimestamp();
    public int getExternalTimestamp();
    public ExternalMessageType getExternalMessageType();
}
