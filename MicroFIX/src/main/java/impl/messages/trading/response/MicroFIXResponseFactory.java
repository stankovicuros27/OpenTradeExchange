package impl.messages.trading.response;

import api.messages.trading.MicroFIXSide;
import api.messages.trading.response.MicroFIXResponseType;
import api.messages.trading.response.IMicroFIXResponse;
import api.messages.trading.response.IMicroFIXResponseFactory;
import api.time.ITimestampProvider;
import impl.time.InstantTimestampProvider;

public class MicroFIXResponseFactory implements IMicroFIXResponseFactory {

    private static final int UNUSED_FIELD_VALUE = -1;

    private final ITimestampProvider timestampProvider = new InstantTimestampProvider();

    @Override
    public IMicroFIXResponse getPlacedOrderResponse(String bookID, int userID, int orderID) {
        int timestamp = timestampProvider.getTimestampNow();
        return new MicroFIXResponse(bookID, userID, orderID, UNUSED_FIELD_VALUE, null, UNUSED_FIELD_VALUE, timestamp, UNUSED_FIELD_VALUE, MicroFIXResponseType.PLACED_ORDER);
    }

    @Override
    public IMicroFIXResponse getReceivedPlaceOrderAckResponse(String bookID, int userID, int orderID, double price, MicroFIXSide side, int volume, int externalTimestamp) {
        int timestamp = timestampProvider.getTimestampNow();
        return new MicroFIXResponse(bookID, userID, orderID, price, side, volume, timestamp, externalTimestamp, MicroFIXResponseType.RECEIVED_PLACE_ORDER_ACK);
    }

    @Override
    public IMicroFIXResponse getCancelledOrderResponse(String bookID, int userID, int orderID) {
        int timestamp = timestampProvider.getTimestampNow();
        return new MicroFIXResponse(bookID, userID, orderID, UNUSED_FIELD_VALUE, null, UNUSED_FIELD_VALUE, timestamp, UNUSED_FIELD_VALUE, MicroFIXResponseType.CANCELLED_ORDER);
    }

    @Override
    public IMicroFIXResponse getReceivedCancelOrderAckResponse(String bookID, int userID, int orderID, int externalTimestamp) {
        int timestamp = timestampProvider.getTimestampNow();
        return new MicroFIXResponse(bookID, userID, orderID, UNUSED_FIELD_VALUE, null, UNUSED_FIELD_VALUE, timestamp, externalTimestamp, MicroFIXResponseType.RECEIVED_CANCEL_ORDER_ACK);
    }

    @Override
    public IMicroFIXResponse getClosedOrderResponse(String bookID, int userID, int orderID) {
        int timestamp = timestampProvider.getTimestampNow();
        return new MicroFIXResponse(bookID, userID, orderID, UNUSED_FIELD_VALUE, null, UNUSED_FIELD_VALUE, timestamp, UNUSED_FIELD_VALUE, MicroFIXResponseType.CLOSED_ORDER);
    }

    @Override
    public IMicroFIXResponse getTradeResponse(String bookID, int userID, int orderID, double price, MicroFIXSide side, int tradedVolume) {
        int timestamp = timestampProvider.getTimestampNow();
        return new MicroFIXResponse(bookID, userID, orderID, price, side, tradedVolume, timestamp, UNUSED_FIELD_VALUE, MicroFIXResponseType.TRADE);
    }

    @Override
    public IMicroFIXResponse getErrorAckResponse(String bookID, int userID, double price, MicroFIXSide side, int volume, int externalTimestamp) {
        int timestamp = timestampProvider.getTimestampNow();
        return new MicroFIXResponse(bookID, userID, UNUSED_FIELD_VALUE, price, side, volume, timestamp, externalTimestamp, MicroFIXResponseType.ERROR);
    }

    @Override
    public IMicroFIXResponse getErrorResponse(String bookID, int userID, int orderID) {
        int timestamp = timestampProvider.getTimestampNow();
        return new MicroFIXResponse(bookID, userID, orderID, UNUSED_FIELD_VALUE, null, UNUSED_FIELD_VALUE, timestamp, UNUSED_FIELD_VALUE, MicroFIXResponseType.ERROR);
    }

}
