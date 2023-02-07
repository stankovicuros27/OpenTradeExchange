package impl.messages.internal.requests;

import api.messages.internal.requests.ICancelOrderRequest;
import api.messages.internal.requests.RequestType;

public class CancelOrderRequest implements ICancelOrderRequest {

    private final int userID;
    private final int orderID;
    private final int timestamp;

    public CancelOrderRequest(int userID, int orderID, int timestamp) {
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
    public int getTimestamp() {
        return timestamp;
    }

}
