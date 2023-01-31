package performance;

import api.core.IOrderBook;
import api.core.IOrderRequestFactory;
import api.core.Side;
import api.messages.requests.ICancelOrderRequest;
import api.messages.requests.IPlaceOrderRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LiquidityProvider implements Runnable {

    private static int GLOBAL_ID = 0;

    private final int id = GLOBAL_ID++;
    private final IOrderBook orderBook;
    private final IOrderRequestFactory orderRequestFactory;
    private final double priceBase;
    private final double priceDeviation;
    private final int volumeBase;
    private final int volumeDeviation;
    private final Random random = new Random();
    private final List<IPlaceOrderRequest> placeOrderRequests = new ArrayList<>();
    private int requestCnt = 0;

    public LiquidityProvider(IOrderBook orderBook, IOrderRequestFactory orderRequestFactory, double priceBase, double priceDeviation, int volumeBase, int volumeDeviation) {
        this.orderBook = orderBook;
        this.orderRequestFactory = orderRequestFactory;
        this.priceBase = priceBase;
        this.priceDeviation = priceDeviation;
        this.volumeBase = volumeBase;
        this.volumeDeviation = volumeDeviation;
    }

    @Override
    public void run() {
        while(true) {
            sendRequest();
            if (++requestCnt % 1000000 == 0) {
                requestCnt = 0;
                System.out.println("Liquidity Provider {" + id + "} sent 1M requests!");
            }
        }
    }

    private void sendRequest() {
        int randInt = random.nextInt(100) + 1;
        if (randInt > placeOrderRequests.size()) {
            placeOrderRequest();
        } else {
            cancelOrderRequest();
        }
    }

    private void placeOrderRequest() {
        IPlaceOrderRequest placeOrderRequest = orderRequestFactory.createPlaceOrderRequest(id, getNextPrice(), getNextSide(), getNextVolume());
        placeOrderRequests.add(placeOrderRequest);
        orderBook.placeOrder(placeOrderRequest);
    }

    private void cancelOrderRequest() {
        int randInt = random.nextInt(placeOrderRequests.size());
        IPlaceOrderRequest placeOrderRequest = placeOrderRequests.remove(randInt);
        ICancelOrderRequest cancelOrderRequest = orderRequestFactory.createCancelOrderRequest(placeOrderRequest.getUserID(), placeOrderRequest.getOrderID());
        orderBook.cancelOrder(cancelOrderRequest);
    }

    private Side getNextSide() {
        return random.nextDouble() > 0.5 ? Side.BUY : Side.SELL;
    }

    private double getNextPrice() {
        // Round 2 decimals
        return Math.round((priceBase + priceDeviation * random.nextDouble() * 100)) / 100.0;
    }

    private int getNextVolume() {
        return volumeBase + (int)(volumeDeviation * random.nextDouble());
    }

}
