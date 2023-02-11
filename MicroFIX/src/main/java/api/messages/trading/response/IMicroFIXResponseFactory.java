package api.messages.trading.response;

import api.messages.trading.MicroFIXSide;

public interface IMicroFIXResponseFactory {
    public IMicroFIXResponse getPlacedOrderResponse(String bookID, int userID, int orderID);
    public IMicroFIXResponse getReceivedPlaceOrderAckResponse(String bookID, int userID, int orderID, double price, MicroFIXSide side, int volume, int externalTimestamp);
    public IMicroFIXResponse getCancelledOrderResponse(String bookID, int userID, int orderID);
    public IMicroFIXResponse getReceivedCancelOrderAckResponse(String bookID, int userID, int orderID, int externalTimestamp);
    public IMicroFIXResponse getClosedOrderResponse(String bookID, int userID, int orderID);
    public IMicroFIXResponse getTradeResponse(String bookID, int userID, int orderID, double price, MicroFIXSide side, int tradedVolume);
    public IMicroFIXResponse getErrorAckResponse(String bookID, int userID, double price, MicroFIXSide side, int volume, int externalTimestamp);
    public IMicroFIXResponse getErrorResponse(String bookID, int userID, int orderID);

}
