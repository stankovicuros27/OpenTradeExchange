package networking.messages.requests;

public class NetworkCancelOrderRequest implements INetworkRequest {

    private final int userID;
    private final int orderID;

    public NetworkCancelOrderRequest(int userID, int orderID) {
        this.userID = userID;
        this.orderID = orderID;
    }

    public int getUserID() {
        return userID;
    }

    public int getOrderID() {
        return orderID;
    }

    @Override
    public NetworkRequestType getNetworkRequestType() {
        return NetworkRequestType.CANCEL;
    }

}
