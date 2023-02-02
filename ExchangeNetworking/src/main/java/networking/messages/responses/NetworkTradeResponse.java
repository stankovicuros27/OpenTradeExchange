package networking.messages.responses;

public class NetworkTradeResponse implements INetworkResponse {

    private final int buyUserID;
    private final int buyOrderID;
    private final int sellUserID;
    private final int sellOrderID;
    private final double price;
    private final int volume;
    private final int timestamp;

    public NetworkTradeResponse(int buyUserID, int buyOrderID, int sellUserID, int sellOrderID, double price, int volume, int timestamp) {
        this.buyUserID = buyUserID;
        this.buyOrderID = buyOrderID;
        this.sellUserID = sellUserID;
        this.sellOrderID = sellOrderID;
        this.price = price;
        this.volume = volume;
        this.timestamp = timestamp;
    }

    public int getBuyUserID() {
        return buyUserID;
    }

    public int getBuyOrderID() {
        return buyOrderID;
    }

    public int getSellUserID() {
        return sellUserID;
    }

    public int getSellOrderID() {
        return sellOrderID;
    }

    public double getPrice() {
        return price;
    }

    public int getVolume() {
        return volume;
    }

    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public NetworkResponseType getType() {
        return NetworkResponseType.TradeResponse;
    }

}
