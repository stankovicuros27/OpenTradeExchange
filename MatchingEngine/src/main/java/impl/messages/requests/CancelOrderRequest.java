package impl.messages.requests;

import api.messages.requests.ICancelOrderRequest;
import api.messages.requests.RequestType;

public class CancelOrderRequest implements ICancelOrderRequest {

    private final String bookID;
    private final int userID;
    private final int orderID;
    private final int timestamp;

    public CancelOrderRequest(String bookID, int userID, int orderID, int timestamp) {
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
    public RequestType getRequestType() {
        return RequestType.CANCEL;
    }

    @Override
    public String getBookID() {
        return bookID;
    }

    @Override
    public int getTimestamp() {
        return timestamp;
    }

}
