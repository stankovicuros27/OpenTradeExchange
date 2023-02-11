package api.messages.trading.request;

import api.messages.trading.MicroFIXSide;

public interface IMicroFIXRequestFactory {
    public IMicroFIXRequest getPlaceOrderRequest(String bookID, int userID, double price, MicroFIXSide side, int volume, int externalTimestamp);
    public IMicroFIXRequest getCancelOrderRequest(String bookID, int userID, int orderID, int externalTimestamp);
}
