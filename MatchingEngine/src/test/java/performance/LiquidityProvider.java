package performance;

import api.core.IOrderBook;
import api.core.IOrderRequestFactory;
import api.core.Side;
import api.messages.requests.ICancelOrderRequest;
import api.messages.requests.IPlaceOrderRequest;
import api.messages.responses.IResponse;
import api.messages.responses.ITradeResponse;
import api.messages.responses.ResponseType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LiquidityProvider implements Runnable {

    private static int GLOBAL_ID = 0;

    private final int id = GLOBAL_ID++;
    private final IOrderBook orderBook;
    private final IOrderRequestFactory orderRequestFactory;
    private final PerformanceDataStore performanceDataStore;
    private final double priceBase;
    private final double priceDeviation;
    private final int volumeBase;
    private final int volumeDeviation;
    private final Random random = new Random();
    private final List<IPlaceOrderRequest> placeOrderRequests = new ArrayList<>();
    private int requestCnt = 0;

    public LiquidityProvider(IOrderBook orderBook, IOrderRequestFactory orderRequestFactory, PerformanceDataStore performanceDataStore, double priceBase, double priceDeviation, int volumeBase, int volumeDeviation) {
        this.orderBook = orderBook;
        this.orderRequestFactory = orderRequestFactory;
        this.performanceDataStore = performanceDataStore;
        this.priceBase = priceBase;
        this.priceDeviation = priceDeviation;
        this.volumeBase = volumeBase;
        this.volumeDeviation = volumeDeviation;
    }

    @Override
    public void run() {
        while(true) {
            List<IResponse> responses = sendRequest();
            performanceDataStore.recordEvents(responses.size());
            for (IResponse response : responses) {
                if (response.getType() == ResponseType.TradeResponse) {
                    ITradeResponse tradeResponse = (ITradeResponse) response;
                    performanceDataStore.recordLastTradePrice(tradeResponse.getPrice());
                    break;
                }
            }
            if (++requestCnt % 1000000 == 0) {
                requestCnt = 0;
                //System.out.println("Liquidity Provider {" + id + "} sent 1M requests!");
            }
        }
    }

    private List<IResponse> sendRequest() {
        int randInt = random.nextInt(100) + 1;
        if (randInt > placeOrderRequests.size()) {
            return placeOrderRequest();
        } else {
            return cancelOrderRequest();
        }
    }

    private List<IResponse> placeOrderRequest() {
        IPlaceOrderRequest placeOrderRequest = orderRequestFactory.createPlaceOrderRequest(id, getNextPrice(), getNextSide(), getNextVolume());
        placeOrderRequests.add(placeOrderRequest);
        return orderBook.placeOrder(placeOrderRequest);
    }

    private List<IResponse> cancelOrderRequest() {
        int randInt = random.nextInt(placeOrderRequests.size());
        IPlaceOrderRequest placeOrderRequest = placeOrderRequests.remove(randInt);
        ICancelOrderRequest cancelOrderRequest = orderRequestFactory.createCancelOrderRequest(placeOrderRequest.getUserID(), placeOrderRequest.getOrderID());
        return List.of(orderBook.cancelOrder(cancelOrderRequest));
    }

    private Side getNextSide() {
        return random.nextDouble() > 0.5 ? Side.BUY : Side.SELL;
    }

    private double getNextPrice() {
        // Round 2 decimals
        double priceAdjustment = priceDeviation * random.nextDouble();
        if (random.nextBoolean()) {
            priceAdjustment = -priceAdjustment;
        }
        return Math.round(((priceBase + priceAdjustment) * 100)) / 100.0;
    }

    private int getNextVolume() {
        int volumeAdjustment = (int)(volumeDeviation * random.nextDouble());
        if (random.nextBoolean()) {
            volumeAdjustment = -volumeAdjustment;
        }
        return volumeBase + volumeAdjustment;
    }

}
