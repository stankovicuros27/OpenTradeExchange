package server.direct;

import api.core.IMatchingEngine;
import api.core.IOrderBook;
import api.messages.info.IOrderBookInfo;

public class ExchangeInfoPublisher implements Runnable {

    private final IMatchingEngine matchingEngine;
    private final BroadcastService broadcastService;
    private final int timeoutMs;

    public ExchangeInfoPublisher(IMatchingEngine matchingEngine, BroadcastService broadcastService, int timeoutMs) {
        this.matchingEngine = matchingEngine;
        this.broadcastService = broadcastService;
        this.timeoutMs = timeoutMs;
    }

    @Override
    public void run() {
        while(true) {
            try {
                for (IOrderBook orderBook : matchingEngine.getAllOrderBooks()) {
                    IOrderBookInfo orderBookInfo = orderBook.getInfo();
                    // TODO map internal to external responses
                    // broadcastService.broadcastMessages(orderBookInfo);
                }
                Thread.sleep(timeoutMs);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
                // TODO handle
            }
        }
    }

}
