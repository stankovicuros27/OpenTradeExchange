package trader.agents.controlled;

import api.messages.external.IExternalCancelOrderRequest;
import api.messages.external.IExternalPlaceOrderRequest;
import api.messages.external.IExternalRequest;
import api.messages.IMessage;
import api.messages.MessageType;
import api.messages.internal.responses.IResponse;
import api.messages.internal.responses.ResponseType;
import api.sides.Side;
import api.time.ITimestampProvider;
import impl.messages.external.ExternalCancelOrderRequest;
import impl.messages.external.ExternalPlaceOrderRequest;
import impl.messages.internal.responses.CancelOrderAckResponse;
import impl.messages.internal.responses.PlaceOrderAckResponse;
import impl.time.InstantTimestampProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ControlledAgentLiquidityProvider extends ControlledTraderAgent {

    private final Random random = new Random();
    private final List<Integer> activeOrderIDs = new ArrayList<>();
    private final ITimestampProvider timestampProvider = new InstantTimestampProvider();

    // TODO CHANGE
    private static final String[] bookIDs = {"Test1", "Test2"};
    private static long bookIDCnt = 0;

    public ControlledAgentLiquidityProvider(double priceBase, double priceDeviation, int volumeBase, int volumeDeviation, int maxOrders) {
        super(priceBase, priceDeviation, volumeBase, volumeDeviation, maxOrders);
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
    public void registerMessages(List<IMessage> messages) {
        for (IMessage message : messages) {
            if (message.getMessageType() != MessageType.RESPONSE) {
                continue;
            }
            IResponse response = (IResponse) message;
            if (response.getType() == ResponseType.PlaceOrderAckResponse) {
                PlaceOrderAckResponse placeOrderAckResponse = (PlaceOrderAckResponse) response;
                activeOrderIDs.add(placeOrderAckResponse.getOrderID());
            } else if (response.getType() == ResponseType.CancelOrderAckResponse) {
                CancelOrderAckResponse cancelOrderAckResponse = (CancelOrderAckResponse) response;
                activeOrderIDs.remove(Integer.valueOf(cancelOrderAckResponse.getOrderID()));
            }
        }
    }

    @Override
    public void registerMessage(IMessage message) {
        if (message.getMessageType() != MessageType.RESPONSE) {
            return;
        }
        IResponse response = (IResponse) message;
        if (response.getType() == ResponseType.PlaceOrderAckResponse) {
            PlaceOrderAckResponse placeOrderAckResponse = (PlaceOrderAckResponse) response;
            activeOrderIDs.add(placeOrderAckResponse.getOrderID());
        } else if (response.getType() == ResponseType.CancelOrderAckResponse) {
            CancelOrderAckResponse cancelOrderAckResponse = (CancelOrderAckResponse) response;
            activeOrderIDs.remove(Integer.valueOf(cancelOrderAckResponse.getOrderID()));
        }
    }

    private IExternalPlaceOrderRequest getExternalPlaceOrderRequest() {
        double price = getNextPrice();
        Side side = getNextSide();
        int volume = getNextVolume();
        String bookID = bookIDs[(int) (++bookIDCnt % 2)];
        return new ExternalPlaceOrderRequest(bookID, id, price, side, volume, timestampProvider.getTimestampNow());
    }

    private IExternalCancelOrderRequest getExternalCancelOrderRequest() {
        int randIndex = random.nextInt(activeOrderIDs.size());
        int orderID = activeOrderIDs.get(randIndex);
        String bookID = bookIDs[(int) (++bookIDCnt % 2)];
        return new ExternalCancelOrderRequest(bookID, id, orderID, timestampProvider.getTimestampNow());
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
