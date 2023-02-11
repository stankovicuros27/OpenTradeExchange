package api.messages.trading.response;

public enum MicroFIXResponseType {
    PLACED_ORDER,
    RECEIVED_PLACE_ORDER_ACK,
    CANCELLED_ORDER,
    RECEIVED_CANCEL_ORDER_ACK,
    CLOSED_ORDER,
    TRADE,
    ERROR
}
