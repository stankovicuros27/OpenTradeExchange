package api.messages.external.request;

import api.messages.external.ExternalSide;

public interface IExternalRequestFactory {
    public IExternalRequest getPlaceOrderRequest(String bookID, int userID, double price, ExternalSide side, int volume, int externalTimestamp);
    public IExternalRequest getCancelOrderRequest(String bookID, int userID, int orderID, int externalTimestamp);
}
