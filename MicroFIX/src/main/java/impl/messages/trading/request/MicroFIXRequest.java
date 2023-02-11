package impl.messages.trading.request;

import api.messages.trading.MicroFIXMessageType;
import api.messages.trading.MicroFIXSide;
import api.messages.trading.request.MicroFIXRequestType;
import api.messages.trading.request.IMicroFIXRequest;
import impl.messages.trading.MicroFIXMessage;

public class MicroFIXRequest extends MicroFIXMessage implements IMicroFIXRequest {

    protected final MicroFIXRequestType MicroFIXRequestType;

    MicroFIXRequest(String bookID, int userID, int orderID, double price, MicroFIXSide side, int volume, int timestamp, int externalTimestamp, MicroFIXRequestType MicroFIXRequestType) {
        super(bookID, userID, orderID, price, side, volume, timestamp, externalTimestamp);
        this.MicroFIXRequestType = MicroFIXRequestType;
    }

    @Override
    public MicroFIXMessageType getExternalMessageType() {
        return MicroFIXMessageType.MICRO_FIX_REQUEST;
    }

    @Override
    public MicroFIXRequestType getExternalRequestType() {
        return MicroFIXRequestType;
    }

}
