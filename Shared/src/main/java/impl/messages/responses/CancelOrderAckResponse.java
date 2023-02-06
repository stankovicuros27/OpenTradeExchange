package impl.messages.responses;

import api.messages.responses.ICancelOrderAckResponse;
import api.messages.responses.ResponseType;

public class CancelOrderAckResponse implements ICancelOrderAckResponse {

    private final int userID;
    private final int orderID;
    private final int timestamp;

    public CancelOrderAckResponse(int userID, int orderID, int timestamp) {
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
    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public ResponseType getType() {
        return ResponseType.CancelOrderAckResponse;
    }

}
