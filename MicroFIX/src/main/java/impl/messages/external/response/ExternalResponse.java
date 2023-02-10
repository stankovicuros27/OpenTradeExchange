package impl.messages.external.response;

import api.messages.external.ExternalMessageType;
import api.messages.external.ExternalSide;
import api.messages.external.response.ExternalResponseType;
import api.messages.external.response.IExternalResponse;
import impl.messages.external.ExternalMessage;

public class ExternalResponse extends ExternalMessage implements IExternalResponse {

    protected final ExternalResponseType externalResponseType;

    ExternalResponse(String bookID, int userID, int orderID, double price, ExternalSide side, int volume, int timestamp, int externalTimestamp, ExternalResponseType externalResponseType) {
        super(bookID, userID, orderID, price, side, volume, timestamp, externalTimestamp);
        this.externalResponseType = externalResponseType;
    }

    @Override
    public ExternalMessageType getExternalMessageType() {
        return ExternalMessageType.EXTERNAL_RESPONSE;
    }

    @Override
    public ExternalResponseType getExternalResponseType() {
        return externalResponseType;
    }
}
