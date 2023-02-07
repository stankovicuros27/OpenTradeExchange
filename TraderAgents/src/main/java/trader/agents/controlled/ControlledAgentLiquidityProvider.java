package trader.agents.controlled;

import trader.agents.controlled.ControlledTraderAgent;
import trader.messages.CancelOrderRequestInfo;
import trader.messages.ITraderRequestInfo;
import trader.messages.PlaceOrderRequestInfo;
import api.messages.responses.IResponse;
import api.messages.responses.ResponseType;
import api.sides.Side;
import impl.messages.responses.CancelOrderAckResponse;
import impl.messages.responses.PlaceOrderAckResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ControlledAgentLiquidityProvider extends ControlledTraderAgent {

    private final Random random = new Random();
    private final List<Integer> activeOrderIDs = new ArrayList<>();

    public ControlledAgentLiquidityProvider(double priceBase, double priceDeviation, int volumeBase, int volumeDeviation, int maxOrders) {
        super(priceBase, priceDeviation, volumeBase, volumeDeviation, maxOrders);
    }

    @Override
    public ITraderRequestInfo getNextRequest() {
        int randInt = random.nextInt(maxOrders) + 1;
        if (randInt > activeOrderIDs.size()) {
            return getPlaceOrderRequestInfo();
        } else {
            return getCancelOrderRequestInfo();
        }
    }

    @Override
    public void registerResponses(List<IResponse> responses) {
        for (IResponse response : responses) {
            if (response.getType() == ResponseType.PlaceOrderAckResponse) {
                PlaceOrderAckResponse placeOrderAckResponse = (PlaceOrderAckResponse) response;
                activeOrderIDs.add(placeOrderAckResponse.getOrderID());
            } else if (response.getType() == ResponseType.CancelOrderAckResponse) {
                CancelOrderAckResponse cancelOrderAckResponse = (CancelOrderAckResponse) response;
                activeOrderIDs.remove(Integer.valueOf(cancelOrderAckResponse.getOrderID()));
            }
        }
    }

    private PlaceOrderRequestInfo getPlaceOrderRequestInfo() {
        double price = getNextPrice();
        Side side = getNextSide();
        int volume = getNextVolume();
        return new PlaceOrderRequestInfo(id, price, side, volume);
    }

    private CancelOrderRequestInfo getCancelOrderRequestInfo() {
        int randIndex = random.nextInt(activeOrderIDs.size());
        int orderID = activeOrderIDs.get(randIndex);
        return new CancelOrderRequestInfo(id, orderID);
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
