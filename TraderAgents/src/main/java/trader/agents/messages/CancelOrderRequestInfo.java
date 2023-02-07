package trader.agents.messages;

public class CancelOrderRequestInfo implements ITraderRequestInfo {

    private final int userID;
    private final int orderID;

    public CancelOrderRequestInfo(int userID, int orderID) {
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
    public TraderRequestInfoType getType() {
        return TraderRequestInfoType.CANCEL;
    }
}
