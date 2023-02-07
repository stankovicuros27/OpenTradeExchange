package trader.messages;

import api.sides.Side;

public class PlaceOrderRequestInfo implements ITraderRequestInfo {

    private final int userID;
    private final double price;
    private final Side side;
    private final int volume;

    public PlaceOrderRequestInfo(int userID, double price, Side side, int volume) {
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

    public Side getSide() {
        return side;
    }

    public int getVolume() {
        return volume;
    }

    @Override
    public TraderRequestInfoType getType() {
        return TraderRequestInfoType.PLACE;
    }
}
