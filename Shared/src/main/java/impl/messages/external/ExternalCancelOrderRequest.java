package impl.messages.external;

import api.messages.external.ExternalRequestType;
import api.messages.external.IExternalCancelOrderRequest;

public class ExternalCancelOrderRequest implements IExternalCancelOrderRequest {

    private final int userID;
    private final int orderID;
    private final int timestamp;

    public ExternalCancelOrderRequest(int userID, int orderID, int timestamp) {
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
    public ExternalRequestType getExternalRequestType() {
        return ExternalRequestType.CANCEL;
    }

    @Override
    public int getTimestamp() {
        return timestamp;
    }

}
