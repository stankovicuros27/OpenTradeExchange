package performance;

import api.core.IOrderBook;
import api.core.Side;
import api.messages.info.ILimitInfo;
import api.messages.info.IOrderBookInfo;

public class OrderBookObserver implements Runnable {

    private static final int NUMBER_OF_LIMITS_DISPLAYED = 10;
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

            System.out.print(ConsoleColors.GREEN);
            int displayBuySideNumber = Math.min(orderBookInfo.getLimitCollectionInfo(Side.BUY).getLimitInfos().size(), NUMBER_OF_LIMITS_DISPLAYED);
            for (int i = orderBookInfo.getLimitCollectionInfo(Side.BUY).getLimitInfos().size() - displayBuySideNumber; i < orderBookInfo.getLimitCollectionInfo(Side.BUY).getLimitInfos().size(); i++) {
                ILimitInfo limitInfo = orderBookInfo.getLimitCollectionInfo(Side.BUY).getLimitInfos().get(i);
                if (i != orderBookInfo.getLimitCollectionInfo(Side.BUY).getLimitInfos().size() - displayBuySideNumber) {
                    System.out.print(",");
                }
                System.out.print(" " + limitInfo.getVolume() + " @ " + limitInfo.getPrice());
            }
            System.out.print(ConsoleColors.RESET);

            System.out.print(ConsoleColors.RED);
            System.out.print(ConsoleColors.CYAN_BOLD + " <- " + ConsoleColors.RESET);
            System.out.print(ConsoleColors.RED + "BUY | SELL" + ConsoleColors.RESET);
            System.out.print(ConsoleColors.CYAN_BOLD + " ->" + ConsoleColors.RESET);
            System.out.print(ConsoleColors.RESET);

            System.out.print(ConsoleColors.BLUE);
            int displaySellSideNumber = Math.min(orderBookInfo.getLimitCollectionInfo(Side.SELL).getLimitInfos().size(), NUMBER_OF_LIMITS_DISPLAYED);
            for (int i = 0; i < displaySellSideNumber; i++) {
                ILimitInfo limitInfo = orderBookInfo.getLimitCollectionInfo(Side.SELL).getLimitInfos().get(i);
                if (i != 0) {
                    System.out.print(",");
                }
                System.out.print(" " + limitInfo.getVolume() + " @ " + limitInfo.getPrice());
            }
            System.out.print(ConsoleColors.RESET);
            System.out.println(" ");

            System.out.println("~~~~~~~~~~~~ xxxxxxxxxxxxxxx ~~~~~~~~~~~~");
        }
    }
}
