package impl.messages.responses;

import api.messages.responses.IPlaceOrderAckResponse;
import api.messages.responses.ResponseType;
import api.sides.Side;

public class PlaceOrderAckResponse implements IPlaceOrderAckResponse {

    private final int userID;
    private final int orderID;
    private final double price;
    private final Side side;
    private final int volume;
    private final int timestamp;

    public PlaceOrderAckResponse(int userID, int orderID, double price, Side side, int volume, int timestamp) {
        this.userID = userID;
        this.orderID = orderID;
        this.price = price;
        this.side = side;
        this.volume = volume;
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
    public double getPrice() {
        return price;
    }

    @Override
    public Side getSide() {
        return side;
    }

    @Override
    public int getVolume() {
        return volume;
    }

    @Override
    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public ResponseType getType() {
        return ResponseType.PlaceOrderAckResponse;
    }

}
