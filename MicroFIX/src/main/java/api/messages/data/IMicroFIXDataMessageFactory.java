package api.messages.data;

public interface IMicroFIXDataMessageFactory {
    public IMicroFIXL1DataMessage getL1DataMessage(String bookID, double bestBuyPrice, int totalBuyVolume, double bestSellPrice, int totalSellVolume, double lastTradePrice);
}
