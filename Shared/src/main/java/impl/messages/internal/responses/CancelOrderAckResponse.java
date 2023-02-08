package impl.messages.internal.responses;

import api.messages.internal.responses.ICancelOrderAckResponse;
import api.messages.internal.responses.ResponseType;

public class CancelOrderAckResponse implements ICancelOrderAckResponse {

    private final String bookID;
    private final int userID;
    private final int orderID;
    private final int timestamp;

    public CancelOrderAckResponse(String bookID, int userID, int orderID, int timestamp) {
        this.bookID = bookID;
        this.userID = userID;
        this.orderID = orderID;
        this.timestamp = timestamp;
    }

    @Override
    public int getUserID() {
        return userID;
    }

    @Override
    public int getOrderID() {
        return orderID;
    }

    @Override
    public String getBookID() {
        return bookID;
    }

    @Override
    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public ResponseType getType() {
        return ResponseType.CancelOrderAckResponse;
    }

}
