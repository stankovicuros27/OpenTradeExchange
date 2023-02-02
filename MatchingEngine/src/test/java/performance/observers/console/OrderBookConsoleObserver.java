package performance.observers.console;

import api.core.IOrderBook;
import api.messages.info.ILimitCollectionInfo;
import api.messages.info.ILimitInfo;
import api.messages.info.IOrderBookInfo;
import api.sides.Side;

public class OrderBookConsoleObserver implements Runnable {

    private static final int NUMBER_OF_LIMITS_DISPLAYED = 10;
    private final IOrderBook orderBook;
    private final int waitTimeMs;

    public OrderBookConsoleObserver(IOrderBook orderBook, int waitTimeMs) {
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
            logInfo(orderBookInfo);
        }
    }

    private void logInfo(IOrderBookInfo orderBookInfo) {
        System.out.println("~~~~~~~~~~~~ ORDER BOOK INFO ~~~~~~~~~~~~");
        System.out.println("Number of orders: " + orderBookInfo.getNumberOfOrders());
        System.out.println("Total volume: " + orderBookInfo.getVolume());
        System.out.println("Buy volume: " + orderBookInfo.getLimitCollectionInfo(Side.BUY).getVolume());
        System.out.println("Sell volume: " + orderBookInfo.getLimitCollectionInfo(Side.SELL).getVolume());
        logOrderBookSide(orderBookInfo.getLimitCollectionInfo(Side.BUY));
        logSideSeparator();
        logOrderBookSide(orderBookInfo.getLimitCollectionInfo(Side.SELL));
        System.out.println(" ");
        System.out.println("~~~~~~~~~~~~ XXXXXXXXXXXXXXX ~~~~~~~~~~~~");
    }

    private void logOrderBookSide(ILimitCollectionInfo limitCollectionInfo) {
        if (limitCollectionInfo.getSide() == Side.BUY) {
            System.out.print(ConsoleColors.GREEN);
            int numberOfLimits = limitCollectionInfo.getLimitInfos().size();
            int displayBuySideNumber = Math.min(numberOfLimits, NUMBER_OF_LIMITS_DISPLAYED);
            for (int i = numberOfLimits - displayBuySideNumber; i < numberOfLimits; i++) {
                ILimitInfo limitInfo = limitCollectionInfo.getLimitInfos().get(i);
                if (i != numberOfLimits - displayBuySideNumber) {
                    System.out.print(", ");
                }
                System.out.print(limitInfo.getVolume() + " @ " + limitInfo.getPrice());
            }
            System.out.print(ConsoleColors.RESET);
        } else {
            System.out.print(ConsoleColors.BLUE);
            int numberOfLimits = limitCollectionInfo.getLimitInfos().size();
            int displaySellSideNumber = Math.min(numberOfLimits, NUMBER_OF_LIMITS_DISPLAYED);
            for (int i = 0; i < displaySellSideNumber; i++) {
                ILimitInfo limitInfo = limitCollectionInfo.getLimitInfos().get(i);
                if (i != 0) {
                    System.out.print(", ");
                }
                System.out.print(limitInfo.getVolume() + " @ " + limitInfo.getPrice());
            }
            System.out.print(ConsoleColors.RESET);
        }
    }

    private void logSideSeparator() {
        System.out.print(ConsoleColors.CYAN_BOLD + " <- " + ConsoleColors.RESET);
        System.out.print(ConsoleColors.RED + "BUY" + ConsoleColors.RESET);
        System.out.print(ConsoleColors.PURPLE + " <|> " + ConsoleColors.RESET);
        System.out.print(ConsoleColors.RED + "SELL" + ConsoleColors.RESET);
        System.out.print(ConsoleColors.CYAN_BOLD + " -> " + ConsoleColors.RESET);
    }
}
