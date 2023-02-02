package impl.messages.responses;

import api.messages.responses.ITradeResponse;
import api.messages.responses.ResponseType;

import java.util.Objects;

public class TradeResponse implements ITradeResponse {

    private final int buyUserID;
    private final int buyOrderID;
    private final int sellUserID;
    private final int sellOrderID;
    private final double price;
    private final int volume;
    private final int timestamp;

    public TradeResponse(int buyUserID, int buyOrderID, int sellUserID, int sellOrderID, double price, int volume, int timestamp) {
        this.buyUserID = buyUserID;
        this.buyOrderID = buyOrderID;
        this.sellUserID = sellUserID;
        this.sellOrderID = sellOrderID;
        this.price = price;
        this.volume = volume;
        this.timestamp = timestamp;
    }

    @Override
    public int getBuyUserID() {
        return buyUserID;
    }

    @Override
    public int getBuyOrderID() {
        return buyOrderID;
    }

    @Override
    public int getSellUserID() {
        return sellUserID;
    }

    @Override
    public int getSellOrderID() {
        return sellOrderID;
    }

    @Override
    public double getPrice() {
        return price;
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
        return ResponseType.TradeResponse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TradeResponse that = (TradeResponse) o;
        return buyUserID == that.buyUserID && buyOrderID == that.buyOrderID && sellUserID == that.sellUserID && sellOrderID == that.sellOrderID && Double.compare(that.price, price) == 0 && volume == that.volume && timestamp == that.timestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(buyUserID, buyOrderID, sellUserID, sellOrderID, price, volume, timestamp);
    }

    @Override
    public String toString() {
        return "TradeResponse{" +
                " buyUserID = " + buyUserID +
                ", buyOrderID = " + buyOrderID +
                ", sellUserID = " + sellUserID +
                ", sellOrderID = " + sellOrderID +
                ", price = " + price +
                ", volume = " + volume +
                ", timestamp = " + timestamp +
                " }";
    }
}
