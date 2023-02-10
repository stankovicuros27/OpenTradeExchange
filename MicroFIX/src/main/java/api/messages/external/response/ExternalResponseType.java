package api.messages.external.response;

public enum ExternalResponseType {
    PLACED_ORDER,
    RECEIVED_PLACE_ORDER_ACK,
    CANCELLED_ORDER,
    RECEIVED_CANCEL_ORDER_ACK,
    CLOSED_ORDER,
    TRADE,
    ERROR
}
