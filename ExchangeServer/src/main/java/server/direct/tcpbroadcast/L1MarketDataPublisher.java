package server.direct.tcpbroadcast;

import api.core.IMatchingEngine;
import api.core.IOrderBook;
import api.core.Side;
import api.messages.data.IMicroFIXDataMessageFactory;
import api.messages.data.IMicroFIXL1DataMessage;
import api.messages.info.IOrderBookInfo;
import impl.messages.data.MicroFIXDataMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class L1MarketDataPublisher implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(L1MarketDataPublisher.class);

    private final IMatchingEngine matchingEngine;
    private final L1MarketDataSenderService senderService;
    private final int l1TimeoutMS;
    private final IMicroFIXDataMessageFactory microFIXDataMessageFactory = new MicroFIXDataMessageFactory();


    public L1MarketDataPublisher(IMatchingEngine matchingEngine, L1MarketDataSenderService senderService, int l1TimeoutMS) {
        this.matchingEngine = matchingEngine;
        this.senderService = senderService;
        this.l1TimeoutMS = l1TimeoutMS;
    }

    @Override
    public void run() {
        LOGGER.info("Starting L1 Data TCP publishing");
        while (true) {
            for (IOrderBook orderBook : matchingEngine.getAllOrderBooks()) {
                IMicroFIXL1DataMessage microFIXL1DataMessage = getL1DataMessage(orderBook);
                senderService.distributeMessage(microFIXL1DataMessage);
            }
            try {
                Thread.sleep(l1TimeoutMS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private IMicroFIXL1DataMessage getL1DataMessage(IOrderBook orderBook) {
        String bookID = orderBook.getBookID();
        IOrderBookInfo orderBookInfo = orderBook.getInfo();
        double bestBuyPrice = orderBookInfo.getBestPrice(Side.BUY);
        int totalBuyVolume = orderBookInfo.getLimitCollectionInfo(Side.BUY).getVolume();
        double bestSellPrice = orderBookInfo.getBestPrice(Side.SELL);
        int totalSellVolume = orderBookInfo.getLimitCollectionInfo(Side.SELL).getVolume();
        double lastTradePrice = orderBookInfo.getLastTradePrice();
        return microFIXDataMessageFactory.getL1DataMessage(bookID, bestBuyPrice, totalBuyVolume, bestSellPrice, totalSellVolume, lastTradePrice);
    }
}
