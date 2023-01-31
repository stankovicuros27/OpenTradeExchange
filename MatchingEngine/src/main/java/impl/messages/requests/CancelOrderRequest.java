package impl.messages.requests;

import api.messages.requests.ICancelOrderRequest;

public class CancelOrderRequest implements ICancelOrderRequest {

    private final int userID;
    private final int orderID;

    public CancelOrderRequest(int userID, int orderID) {
        this.userID = userID;
        this.orderID = orderID;
    }

    @Override
    public int getUserID() {
        return userID;
    }

    @Override
    public int getOrderID() {
        return orderID;
    }
}
