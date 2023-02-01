package performance;

public class PerformanceDataStore {

    private int eventCnt = 0;
    private int placeOrderCnt = 0;
    private int cancelOrderCnt = 0;
    private int tradeCnt = 0;
    private double lastTradePrice = 0;

    public synchronized void recordEvents(int numberOfEvents) {
        eventCnt += numberOfEvents;
    }

    public synchronized void recordLastTradePrice(double tradePrice) {
        lastTradePrice = tradePrice;
    }

    public synchronized double getLastTradePrice() {
        return lastTradePrice;
    }

    public synchronized int getAndResetEventCnt() {
        int x = eventCnt;
        eventCnt = 0;
        return x;
    }

}
