package impl.messages.external;

import api.messages.external.ExternalRequestType;
import api.messages.external.IExternalCancelOrderRequest;

public class ExternalCancelOrderRequest implements IExternalCancelOrderRequest {

    private final String bookID;
    private final int userID;
    private final int orderID;
    private final int timestamp;

    public ExternalCancelOrderRequest(String bookID, int userID, int orderID, int timestamp) {
        this.bookID = bookID;
        this.userID = userID;
        this.orderID = orderID;
        this.timestamp = timestamp;
    }

    @Override
    public String getBookID() {
        return bookID;
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
    public ExternalRequestType getExternalRequestType() {
        return ExternalRequestType.CANCEL;
    }

    @Override
    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "ExternalCancelOrderRequest{" +
                "bookID='" + bookID + '\'' +
                ", userID=" + userID +
                ", orderID=" + orderID +
                ", timestamp=" + timestamp +
                '}';
    }
}
