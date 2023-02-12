package api.messages.data;

public interface IMicroFIXL1DataMessage extends IMicroFIXDataMessage {
    public double getBestBuyPrice();
    public int getTotalBuyVolume();
    public double getBestSellPrice();
    public int getTotalSellVolume();
    public double getLastTradePrice();
}
