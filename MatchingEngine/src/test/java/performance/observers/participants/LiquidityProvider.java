package performance.observers.participants;

import api.core.IOrderBook;
import api.core.IOrderRequestFactory;
import api.core.Side;
import api.messages.requests.ICancelOrderRequest;
import api.messages.requests.IPlaceOrderRequest;
import api.messages.responses.*;
import performance.PerformanceDataStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LiquidityProvider implements Runnable {

    private static int GLOBAL_ID = 0;

    private final int id = GLOBAL_ID++;
    private final IOrderBook orderBook;
    private final IOrderRequestFactory orderRequestFactory;
    private final PerformanceDataStore performanceDataStore;
    private final double priceBaseInitial;
    private double priceBase;
    private final double priceDeviation;
    private final int volumeBaseInitial;
    private int volumeBase;
    private final int volumeDeviation;
    private final Random random = new Random();
    private final List<IPlaceOrderRequest> placeOrderRequests = new ArrayList<>();
    private int requestCnt = 0;

    public LiquidityProvider(IOrderBook orderBook, IOrderRequestFactory orderRequestFactory, PerformanceDataStore performanceDataStore, double priceBaseInitial, double priceDeviation, int volumeBaseInitial, int volumeDeviation) {
        this.orderBook = orderBook;
        this.orderRequestFactory = orderRequestFactory;
        this.performanceDataStore = performanceDataStore;
        this.priceBaseInitial = priceBaseInitial;
        this.priceDeviation = priceDeviation;
        this.volumeBaseInitial = volumeBaseInitial;
        this.volumeDeviation = volumeDeviation;

        this.priceBase = priceBaseInitial;
        this.volumeBase = volumeBaseInitial;
    }

    @Override
    public void run() {
        while(true) {
            List<IResponse> responses = sendRequest();
            updatePerformanceDataStore(responses);
            if (++requestCnt % 100000 == 0) {
                requestCnt = 0;
                //System.out.println("Liquidity Provider {" + id + "} sent 1M requests!");
            }
        }
    }

    public void updatePriceBase(double priceBase) {
        this.priceBase = priceBase;
    }

    public void updateVolumeBase(int volumeBase) {
        this.volumeBase = volumeBase;
    }

    public double getPriceBase() {
        return priceBase;
    }

    public int getVolumeBase() {
        return volumeBase;
    }

    private void updatePerformanceDataStore(List<IResponse> responses) {
        performanceDataStore.recordEvents(responses.size());
        int placeOrderCnt = 0;
        int cancelOrderCnt = 0;
        int closedOrderCnt = 0;
        int tradeCnt = 0;
        double lastTradePrice = -1;
        for (IResponse response : responses) {
            if (response.getType() == ResponseType.TradeResponse) {
                ITradeResponse tradeResponse = (ITradeResponse) response;
                lastTradePrice = tradeResponse.getPrice();
                tradeCnt++;
            } else if (response.getType() == ResponseType.OrderStatusResponse) {
                IOrderStatusResponse orderStatusResponse = (IOrderStatusResponse) response;
                OrderResponseStatus status = orderStatusResponse.getStatus();
                if (status == OrderResponseStatus.CANCELLED_ORDER) {
                    cancelOrderCnt++;
                } else if (status == OrderResponseStatus.PLACED_ORDER) {
                    placeOrderCnt++;
                } else if (status == OrderResponseStatus.CLOSED_ORDER) {
                    closedOrderCnt++;
                }
            }
        }
        performanceDataStore.recordPlaceOrders(placeOrderCnt);
        performanceDataStore.recordCancelOrders(cancelOrderCnt);
        performanceDataStore.recordClosedOrders(closedOrderCnt);
        performanceDataStore.recordTrades(tradeCnt);
        if (lastTradePrice != -1) {
            performanceDataStore.recordLastTradePrice(lastTradePrice);
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
