package agents;

import api.sides.Side;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AgentLiquidityProvider extends TradeAgent {

    private final Random random = new Random();
    private final List<Integer> placeOrderRequestIDs = new ArrayList<>();

    public AgentLiquidityProvider(double priceBase, double priceDeviation, int volumeBase, int volumeDeviation, int maxOrders) {
        super(priceBase, priceDeviation, volumeBase, volumeDeviation, maxOrders);
    }

    @Override
    public void placeOrder() {
        int randInt = random.nextInt(maxOrders) + 1;
        if (randInt > placeOrderRequestIDs.size()) {
            placeOrderRequest();
        } else {
            cancelOrderRequest();
        }
    }

    private void placeOrderRequest() {
        int userID = id;
        double price = getNextPrice();
        Side side = getNextSide();
        int volume = getNextVolume();
    }

    private void cancelOrderRequest() {
        int randIndex = random.nextInt(placeOrderRequestIDs.size());
        int userID = id;
        int orderID = placeOrderRequestIDs.get(randIndex);  // Delete in other place
    }

    private Side getNextSide() {
        return random.nextDouble() > 0.5 ? Side.BUY : Side.SELL;
    }

    private double getNextPrice() {
        double priceAdjustment = priceDeviation * random.nextDouble();
        if (random.nextBoolean()) {
            priceAdjustment = -priceAdjustment;
        }
        return priceBase + priceAdjustment;
    }

    private int getNextVolume() {
        int volumeAdjustment = (int)(volumeDeviation * random.nextDouble());
        if (random.nextBoolean()) {
            volumeAdjustment = -volumeAdjustment;
        }
        return volumeBase + volumeAdjustment;
    }

}
