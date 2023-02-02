package networking.messages.responses;

public class NetworkOrderStatusResponse implements INetworkResponse {

    private final int userID;
    private final int orderID;
    private final NetworkOrderResponseStatus status;

    public NetworkOrderStatusResponse(int userID, int orderID, NetworkOrderResponseStatus status) {
        this.userID = userID;
        this.orderID = orderID;
        this.status = status;
    }

    public int getUserID() {
        return userID;
    }

    public int getOrderID() {
        return orderID;
    }

    public NetworkOrderResponseStatus getStatus() {
        return status;
    }

    @Override
    public NetworkResponseType getType() {
        return NetworkResponseType.OrderStatusResponse;
    }

}
