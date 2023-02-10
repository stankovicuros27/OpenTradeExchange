package trader.agents.controlled;

import api.messages.external.ExternalSide;
import api.messages.external.request.IExternalRequest;
import api.messages.external.request.IExternalRequestFactory;
import api.messages.external.response.ExternalResponseType;
import api.messages.external.response.IExternalResponse;
import api.time.ITimestampProvider;
import impl.messages.external.request.ExternalRequestFactory;
import impl.time.InstantTimestampProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ControlledAgentLiquidityProvider extends ControlledTraderAgent {

    private final Random random = new Random(System.currentTimeMillis());
    private final List<Integer> activeOrderIDs = new ArrayList<>();
    private final ITimestampProvider timestampProvider = new InstantTimestampProvider();
    private final IExternalRequestFactory externalRequestFactory = new ExternalRequestFactory();

    public ControlledAgentLiquidityProvider(String bookID, double priceBase, double priceDeviation, int volumeBase, int volumeDeviation, int maxOrders) {
        super(bookID, priceBase, priceDeviation, volumeBase, volumeDeviation, maxOrders);
    }

    @Override
    public IExternalRequest getNextRequest() {
        int randInt = random.nextInt(maxOrders) + 1;
        if (randInt > activeOrderIDs.size()) {
            return getExternalPlaceOrderRequest();
        } else {
            return getExternalCancelOrderRequest();
        }
    }

    @Override
    public void registerResponses(List<IExternalResponse> messages) {
        for (IExternalResponse message : messages) {
            registerResponse(message);
        }
    }

    @Override
    public void registerResponse(IExternalResponse externalResponse) {
        if (externalResponse.getUserID() != id) {
            return;
        }
        System.out.println(externalResponse);   // TODO delete
        if (externalResponse.getExternalResponseType() == ExternalResponseType.RECEIVED_PLACE_ORDER_ACK) {
            activeOrderIDs.add(externalResponse.getOrderID());
        } else if (externalResponse.getExternalResponseType() == ExternalResponseType.RECEIVED_CANCEL_ORDER_ACK) {
            activeOrderIDs.remove(Integer.valueOf(externalResponse.getOrderID()));
        }
    }

    private IExternalRequest getExternalPlaceOrderRequest() {
        double price = getNextPrice();
        ExternalSide side = getNextSide();
        int volume = getNextVolume();
        return externalRequestFactory.getPlaceOrderRequest(bookID, id, price, side, volume, timestampProvider.getTimestampNow());
    }

    private IExternalRequest getExternalCancelOrderRequest() {
        int randIndex = random.nextInt(activeOrderIDs.size());
        int orderID = activeOrderIDs.get(randIndex);
        return externalRequestFactory.getCancelOrderRequest(bookID, id, orderID, timestampProvider.getTimestampNow());
    }

    private ExternalSide getNextSide() {
        return random.nextDouble() > 0.5 ? ExternalSide.BUY : ExternalSide.SELL;
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
