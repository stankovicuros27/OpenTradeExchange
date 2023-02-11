package impl.messages.data;

import api.messages.data.IMicroFIXL1DataMessage;

public class MicroFIXL1DataMessage implements IMicroFIXL1DataMessage {

    private final String bookID;
    private final int timestamp;
    private final double bestBuyPrice;
    private final int totalBuyVolume;
    private final double bestSellPrice;
    private final int totalSellVolume;
    private final double lastTradePrice;

    MicroFIXL1DataMessage(String bookID, int timestamp, double bestBuyPrice, int totalBuyVolume, double bestSellPrice, int totalSellVolume, double lastTradePrice) {
        this.bookID = bookID;
        this.timestamp = timestamp;
        this.bestBuyPrice = bestBuyPrice;
        this.totalBuyVolume = totalBuyVolume;
        this.bestSellPrice = bestSellPrice;
        this.totalSellVolume = totalSellVolume;
        this.lastTradePrice = lastTradePrice;
    }

    @Override
    public String getBookID() {
        return bookID;
    }

    @Override
    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public int getSizeInBytes() {
        return bookID.length() * 2 + 4 + 8 + 4 + 8 + 4 + 8;
    }

    @Override
    public double getBestBuyPrice() {
        return bestBuyPrice;
    }

    @Override
    public int getTotalBuyVolume() {
        return totalBuyVolume;
    }

    @Override
    public double getBestSellPrice() {
        return bestSellPrice;
    }

    @Override
    public int getTotalSellVolume() {
        return totalSellVolume;
    }

    @Override
    public double getLastTradePrice() {
        return lastTradePrice;
    }

    @Override
    public String toString() {
        return "MicroFIXL1DataMessage{" +
                "bookID='" + bookID + '\'' +
                ", timestamp=" + timestamp +
                ", bestBuyPrice=" + bestBuyPrice +
                ", totalBuyVolume=" + totalBuyVolume +
                ", bestSellPrice=" + bestSellPrice +
                ", totalSellVolume=" + totalSellVolume +
                ", lastTradePrice=" + lastTradePrice +
                '}';
    }
}
