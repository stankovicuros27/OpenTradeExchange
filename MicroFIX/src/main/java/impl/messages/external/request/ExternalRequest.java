package impl.messages.external.request;

import api.messages.external.ExternalMessageType;
import api.messages.external.ExternalSide;
import api.messages.external.request.ExternalRequestType;
import api.messages.external.request.IExternalRequest;
import impl.messages.external.ExternalMessage;

public class ExternalRequest extends ExternalMessage implements IExternalRequest {

    protected final ExternalRequestType externalRequestType;

    ExternalRequest(String bookID, int userID, int orderID, double price, ExternalSide side, int volume, int timestamp, int externalTimestamp, ExternalRequestType externalRequestType) {
        super(bookID, userID, orderID, price, side, volume, timestamp, externalTimestamp);
        this.externalRequestType = externalRequestType;
    }

    @Override
    public ExternalMessageType getExternalMessageType() {
        return ExternalMessageType.EXTERNAL_REQUEST;
    }

    @Override
    public ExternalRequestType getExternalRequestType() {
        return externalRequestType;
    }

}
