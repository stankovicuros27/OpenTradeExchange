package networking.messages.requests;

public class NetworkPlaceOrderRequest implements INetworkRequest {
    private final int userID;
    private final double price;
    private final NetworkRequestSide side;
    private final int volume;

    public NetworkPlaceOrderRequest(int userID, double price, NetworkRequestSide side, int volume) {
        this.userID = userID;
        this.price = price;
        this.side = side;
        this.volume = volume;
    }

    public int getUserID() {
        return userID;
    }

    public double getPrice() {
        return price;
    }

    public NetworkRequestSide getSide() {
        return side;
    }

    public int getVolume() {
        return volume;
    }

    @Override
    public NetworkRequestType getNetworkRequestType() {
        return NetworkRequestType.PLACE;
    }

}
