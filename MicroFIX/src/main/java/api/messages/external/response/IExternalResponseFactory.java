package api.messages.external.response;

import api.messages.external.ExternalSide;

public interface IExternalResponseFactory {
    public IExternalResponse getPlacedOrderResponse(String bookID, int userID, int orderID, double price, ExternalSide side, int volume);
    public IExternalResponse getReceivedPlaceOrderAckResponse(String bookID, int userID, double price, ExternalSide side, int volume, int externalTimestamp);
    public IExternalResponse getCancelledOrderResponse(String bookID, int userID, int orderID, double price, ExternalSide side, int volume);
    public IExternalResponse getReceivedCancelOrderAckResponse(String bookID, int userID, int orderID, double price, ExternalSide side, int volume, int externalTimestamp);
    public IExternalResponse getClosedOrderResponse(String bookID, int userID, int orderID, double price, ExternalSide side, int volume);
    public IExternalResponse getTradeResponse(String bookID, int userID, int orderID, double price, ExternalSide side, int tradedVolume);
    public IExternalResponse getErrorResponse(String bookID, int userID, double price, ExternalSide side, int volume, int externalTimestamp);
}
