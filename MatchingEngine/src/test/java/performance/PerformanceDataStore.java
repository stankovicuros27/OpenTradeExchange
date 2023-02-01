package performance;

public class PerformanceDataStore {

    private int eventCnt = 0;
    private int placeOrderCnt = 0;
    private int cancelOrderCnt = 0;
    private int closedOrderCnt = 0;
    private int tradeCnt = 0;
    private double lastTradePrice = 0;

    public synchronized void recordEvents(int numberOfEvents) {
        eventCnt += numberOfEvents;
    }

    public synchronized int getAndResetEventCnt() {
        int x = eventCnt;
        eventCnt = 0;
        return x;
    }

    public synchronized void recordPlaceOrders(int n) {
        placeOrderCnt += n;
    }

    public synchronized int getAndResetPlaceOrderCnt() {
        int x = placeOrderCnt;
        placeOrderCnt = 0;
        return x;
    }

    public synchronized void recordCancelOrders(int n) {
        cancelOrderCnt += n;
    }

    public synchronized int getAndResetCancelOrderCnt() {
        int x = cancelOrderCnt;
        cancelOrderCnt = 0;
        return x;
    }

    public synchronized void recordClosedOrders(int n) {
        closedOrderCnt += n;
    }

    public synchronized int getAndResetClosedOrderCnt() {
        int x = closedOrderCnt;
        closedOrderCnt = 0;
        return x;
    }

    public synchronized void recordTrades(int n) {
        tradeCnt += n;
    }

    public synchronized int getAndResetTradeCnt() {
        int x = tradeCnt;
        tradeCnt = 0;
        return x;
    }

    public synchronized void recordLastTradePrice(double tradePrice) {
        lastTradePrice = tradePrice;
    }

    public synchronized double getLastTradePrice() {
        return lastTradePrice;
    }

}
