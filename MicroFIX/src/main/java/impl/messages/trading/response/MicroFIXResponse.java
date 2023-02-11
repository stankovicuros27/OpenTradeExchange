package impl.messages.trading.response;

import api.messages.trading.MicroFIXMessageType;
import api.messages.trading.MicroFIXSide;
import api.messages.trading.response.MicroFIXResponseType;
import api.messages.trading.response.IMicroFIXResponse;
import impl.messages.trading.MicroFIXMessage;

public class MicroFIXResponse extends MicroFIXMessage implements IMicroFIXResponse {

    protected final MicroFIXResponseType microFixResponseType;

    MicroFIXResponse(String bookID, int userID, int orderID, double price, MicroFIXSide side, int volume, int timestamp, int externalTimestamp, MicroFIXResponseType microFixResponseType) {
        super(bookID, userID, orderID, price, side, volume, timestamp, externalTimestamp);
        this.microFixResponseType = microFixResponseType;
    }

    @Override
    public MicroFIXMessageType getExternalMessageType() {
        return MicroFIXMessageType.MICRO_FIX_RESPONSE;
    }

    @Override
    public MicroFIXResponseType getExternalResponseType() {
        return microFixResponseType;
    }

    @Override
    public String toString() {
        return "ExternalResponse{" +
                "externalResponseType=" + microFixResponseType +
                ", bookID='" + bookID + '\'' +
                ", userID=" + userID +
                ", orderID=" + orderID +
                ", price=" + price +
                ", side=" + side +
                ", volume=" + volume +
                ", timestamp=" + timestamp +
                ", externalTimestamp=" + externalTimestamp +
                '}';
    }
}
