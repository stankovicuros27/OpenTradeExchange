package trader.agents.controlled;

import api.messages.trading.MicroFIXSide;
import api.messages.trading.request.IMicroFIXRequest;
import api.messages.trading.request.IMicroFIXRequestFactory;
import api.messages.trading.response.MicroFIXResponseType;
import api.messages.trading.response.IMicroFIXResponse;
import api.time.ITimestampProvider;
import impl.messages.trading.request.MicroFIXRequestFactory;
import impl.time.InstantTimestampProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ControlledAgentLiquidityProvider extends ControlledTraderAgent {

    private final Random random = new Random(System.currentTimeMillis());
    private final List<Integer> activeOrderIDs = new ArrayList<>();
    private final ITimestampProvider timestampProvider = new InstantTimestampProvider();
    private final IMicroFIXRequestFactory externalRequestFactory = new MicroFIXRequestFactory();

    public ControlledAgentLiquidityProvider(String bookID, double priceBase, double priceDeviation, int volumeBase, int volumeDeviation, int maxOrders) {
        super(bookID, priceBase, priceDeviation, volumeBase, volumeDeviation, maxOrders);
    }

    @Override
    public int getUserID() {
        return id;
    }

    @Override
    public IMicroFIXRequest getNextRequest() {
        int randInt = random.nextInt(maxOrders) + 1;
        if (randInt > activeOrderIDs.size()) {
            return getExternalPlaceOrderRequest();
        } else {
            return getExternalCancelOrderRequest();
        }
    }

    @Override
    public void registerResponses(List<IMicroFIXResponse> messages) {
        for (IMicroFIXResponse message : messages) {
            registerResponse(message);
        }
    }

    @Override
    public void registerResponse(IMicroFIXResponse externalResponse) {
        if (externalResponse.getUserID() != id) {
            return;
        }
        if (externalResponse.getExternalResponseType() == MicroFIXResponseType.RECEIVED_PLACE_ORDER_ACK) {
            activeOrderIDs.add(externalResponse.getOrderID());
        } else if (externalResponse.getExternalResponseType() == MicroFIXResponseType.RECEIVED_CANCEL_ORDER_ACK) {
            activeOrderIDs.remove(Integer.valueOf(externalResponse.getOrderID()));
        }
    }

    private IMicroFIXRequest getExternalPlaceOrderRequest() {
        double price = getNextPrice();
        MicroFIXSide side = getNextSide();
        int volume = getNextVolume();
        return externalRequestFactory.getPlaceOrderRequest(bookID, id, price, side, volume, timestampProvider.getTimestampNow());
    }

    private IMicroFIXRequest getExternalCancelOrderRequest() {
        int randIndex = random.nextInt(activeOrderIDs.size());
        int orderID = activeOrderIDs.get(randIndex);
        return externalRequestFactory.getCancelOrderRequest(bookID, id, orderID, timestampProvider.getTimestampNow());
    }

    private MicroFIXSide getNextSide() {
        return random.nextDouble() > 0.5 ? MicroFIXSide.BUY : MicroFIXSide.SELL;
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
