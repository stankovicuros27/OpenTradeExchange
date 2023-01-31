package performance;

import api.core.IOrderBook;
import api.core.Side;
import api.messages.info.IOrderBookInfo;

public class OrderBookObserver implements Runnable {

    private final IOrderBook orderBook;
    private final int waitTimeMs;

    public OrderBookObserver(IOrderBook orderBook, int waitTimeMs) {
        this.orderBook = orderBook;
        this.waitTimeMs = waitTimeMs;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(waitTimeMs);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            IOrderBookInfo orderBookInfo = orderBook.getInfo();
            System.out.println("~~~~~~~~~~~~ ORDER BOOK INFO ~~~~~~~~~~~~");
            System.out.println("Number of orders: " + orderBookInfo.getNumberOfOrders());
            System.out.println("Total volume: " + orderBookInfo.getVolume());
            System.out.println("Buy volume: " + orderBookInfo.getLimitCollectionInfo(Side.BUY).getVolume());
            System.out.println("Sell volume: " + orderBookInfo.getLimitCollectionInfo(Side.SELL).getVolume());
            System.out.println("~~~~~~~~~~~~ xxxxxxxxxxxxxxx ~~~~~~~~~~~~");
        }
    }
}
