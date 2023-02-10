package impl.messages.external.response;

import api.messages.external.ExternalSide;
import api.messages.external.response.ExternalResponseType;
import api.messages.external.response.IExternalResponse;
import api.messages.external.response.IExternalResponseFactory;
import api.time.ITimestampProvider;
import impl.time.InstantTimestampProvider;

public class ExternalResponseFactory implements IExternalResponseFactory {

    private static final int UNUSED_FIELD_VALUE = -1;

    private final ITimestampProvider timestampProvider = new InstantTimestampProvider();

    @Override
    public IExternalResponse getPlacedOrderResponse(String bookID, int userID, int orderID) {
        int timestamp = timestampProvider.getTimestampNow();
        return new ExternalResponse(bookID, userID, orderID, UNUSED_FIELD_VALUE, null, UNUSED_FIELD_VALUE, timestamp, UNUSED_FIELD_VALUE, ExternalResponseType.PLACED_ORDER);
    }

    @Override
    public IExternalResponse getReceivedPlaceOrderAckResponse(String bookID, int userID, int orderID, double price, ExternalSide side, int volume, int externalTimestamp) {
        int timestamp = timestampProvider.getTimestampNow();
        return new ExternalResponse(bookID, userID, orderID, price, side, volume, timestamp, externalTimestamp, ExternalResponseType.RECEIVED_PLACE_ORDER_ACK);
    }

    @Override
    public IExternalResponse getCancelledOrderResponse(String bookID, int userID, int orderID) {
        int timestamp = timestampProvider.getTimestampNow();
        return new ExternalResponse(bookID, userID, orderID, UNUSED_FIELD_VALUE, null, UNUSED_FIELD_VALUE, timestamp, UNUSED_FIELD_VALUE, ExternalResponseType.CANCELLED_ORDER);
    }

    @Override
    public IExternalResponse getReceivedCancelOrderAckResponse(String bookID, int userID, int orderID, int externalTimestamp) {
        int timestamp = timestampProvider.getTimestampNow();
        return new ExternalResponse(bookID, userID, orderID, UNUSED_FIELD_VALUE, null, UNUSED_FIELD_VALUE, timestamp, externalTimestamp, ExternalResponseType.RECEIVED_CANCEL_ORDER_ACK);
    }

    @Override
    public IExternalResponse getClosedOrderResponse(String bookID, int userID, int orderID) {
        int timestamp = timestampProvider.getTimestampNow();
        return new ExternalResponse(bookID, userID, orderID, UNUSED_FIELD_VALUE, null, UNUSED_FIELD_VALUE, timestamp, UNUSED_FIELD_VALUE, ExternalResponseType.CLOSED_ORDER);
    }

    @Override
    public IExternalResponse getTradeResponse(String bookID, int userID, int orderID, double price, ExternalSide side, int tradedVolume) {
        int timestamp = timestampProvider.getTimestampNow();
        return new ExternalResponse(bookID, userID, orderID, price, side, tradedVolume, timestamp, UNUSED_FIELD_VALUE, ExternalResponseType.TRADE);
    }

    @Override
    public IExternalResponse getErrorAckResponse(String bookID, int userID, double price, ExternalSide side, int volume, int externalTimestamp) {
        int timestamp = timestampProvider.getTimestampNow();
        return new ExternalResponse(bookID, userID, UNUSED_FIELD_VALUE, price, side, volume, timestamp, externalTimestamp, ExternalResponseType.ERROR);
    }

    @Override
    public IExternalResponse getErrorResponse(String bookID, int userID, int orderID) {
        int timestamp = timestampProvider.getTimestampNow();
        return new ExternalResponse(bookID, userID, orderID, UNUSED_FIELD_VALUE, null, UNUSED_FIELD_VALUE, timestamp, UNUSED_FIELD_VALUE, ExternalResponseType.ERROR);
    }

}
