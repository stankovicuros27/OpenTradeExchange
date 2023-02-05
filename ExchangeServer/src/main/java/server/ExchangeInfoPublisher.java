package server;

import api.core.IOrderBook;
import api.messages.info.IOrderBookInfo;

public class ExchangeInfoPublisher implements Runnable {

    private final IOrderBook orderBook;
    private final BroadcastService broadcastService;
    private final int timeoutMs;

    public ExchangeInfoPublisher(IOrderBook orderBook, BroadcastService broadcastService, int timeoutMs) {
        this.orderBook = orderBook;
        this.broadcastService = broadcastService;
        this.timeoutMs = timeoutMs;
    }

    @Override
    public void run() {
        while(true) {
            try {
                IOrderBookInfo orderBookInfo = orderBook.getInfo();
                broadcastService.broadcastMessages(orderBookInfo);
                Thread.sleep(timeoutMs);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
                // TODO handle
            }
        }
    }

}
