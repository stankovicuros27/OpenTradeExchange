package impl.messages.external;

import api.messages.external.ExternalRequestType;
import api.messages.external.IExternalPlaceOrderRequest;
import api.sides.Side;

public class ExternalPlaceOrderRequest implements IExternalPlaceOrderRequest {

    private final int userID;
    private final double price;
    private final Side side;
    private final int volume;
    private final int timestamp;

    public ExternalPlaceOrderRequest(int userID, double price, Side side, int volume, int timestamp) {
        this.userID = userID;
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
    public ExternalRequestType getExternalRequestType() {
        return ExternalRequestType.PLACE;
    }

    @Override
    public int getTimestamp() {
        return timestamp;
    }

}
