package impl.messages.trading.request;

import api.messages.trading.MicroFIXSide;
import api.messages.trading.request.MicroFIXRequestType;
import api.messages.trading.request.IMicroFIXRequest;
import api.messages.trading.request.IMicroFIXRequestFactory;

public class MicroFIXRequestFactory implements IMicroFIXRequestFactory {

    private static final int UNUSED_FIELD_VALUE = -1;

    @Override
    public IMicroFIXRequest getPlaceOrderRequest(String bookID, int userID, double price, MicroFIXSide side, int volume, int externalTimestamp) {
        return new MicroFIXRequest(bookID, userID, UNUSED_FIELD_VALUE, price, side, volume, UNUSED_FIELD_VALUE, externalTimestamp, MicroFIXRequestType.PLACE);
    }

    @Override
    public IMicroFIXRequest getCancelOrderRequest(String bookID, int userID, int orderID, int externalTimestamp) {
        return new MicroFIXRequest(bookID, userID, orderID, UNUSED_FIELD_VALUE, null, UNUSED_FIELD_VALUE, UNUSED_FIELD_VALUE, externalTimestamp, MicroFIXRequestType.CANCEL);
    }

}
