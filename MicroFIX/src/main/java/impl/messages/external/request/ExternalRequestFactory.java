package impl.messages.external.request;

import api.messages.external.ExternalSide;
import api.messages.external.request.ExternalRequestType;
import api.messages.external.request.IExternalRequest;
import api.messages.external.request.IExternalRequestFactory;

public class ExternalRequestFactory implements IExternalRequestFactory {

    private static final int UNUSED_FIELD_VALUE = -1;

    @Override
    public IExternalRequest getPlaceOrderRequest(String bookID, int userID, double price, ExternalSide side, int volume, int externalTimestamp) {
        return new ExternalRequest(bookID, userID, UNUSED_FIELD_VALUE, price, side, volume, UNUSED_FIELD_VALUE, externalTimestamp, ExternalRequestType.PLACE);
    }

    @Override
    public IExternalRequest getCancelOrderRequest(String bookID, int userID, int orderID, int externalTimestamp) {
        return new ExternalRequest(bookID, userID, orderID, UNUSED_FIELD_VALUE, null, UNUSED_FIELD_VALUE, UNUSED_FIELD_VALUE, externalTimestamp, ExternalRequestType.CANCEL);
    }

}
