package trader.runners.local;

import api.core.Side;
import api.messages.trading.MicroFIXSide;
import api.messages.trading.request.MicroFIXRequestType;
import api.messages.trading.request.IMicroFIXRequest;
import api.messages.trading.response.IMicroFIXResponse;
import api.messages.trading.response.IMicroFIXResponseFactory;
import impl.messages.trading.response.MicroFIXResponseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trader.agents.ITraderAgent;
import api.core.IOrderBook;
import api.messages.requests.ICancelOrderRequest;
import api.messages.requests.IPlaceOrderRequest;
import api.core.IOrderRequestFactory;
import trader.runners.TraderAgentRunner;

import java.util.ArrayList;
import java.util.List;

public class LocalTraderAgentRunner extends TraderAgentRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalTraderAgentRunner.class);

    private final IOrderRequestFactory orderRequestFactory;
    private final IMicroFIXResponseFactory externalResponseFactory = new MicroFIXResponseFactory();
    private final boolean shouldSleep;

    public LocalTraderAgentRunner(ITraderAgent traderAgent, IOrderBook orderBook, int timeoutMs, boolean shouldSleep) {
        super(traderAgent, orderBook, timeoutMs);
        this.orderRequestFactory = orderBook.getOrderRequestFactory();
        this.shouldSleep = shouldSleep;
    }

    @Override
    public void run() {
        LOGGER.info("Starting LocalTradeAgentRunner for OrderBook: " + orderBook.getBookID());
        while(true) {
            IMicroFIXRequest externalRequest = traderAgent.getNextRequest();
            if (externalRequest != null) {
                if (externalRequest.getExternalRequestType() == MicroFIXRequestType.PLACE) {
                    sendPlaceOrderRequest(externalRequest);
                } else if (externalRequest.getExternalRequestType() == MicroFIXRequestType.CANCEL) {
                    sendCancelOrderRequest(externalRequest);
                }
            }
            if (shouldSleep) {
                try {
                    Thread.sleep((long) (Math.random() * timeoutMs));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void sendPlaceOrderRequest(IMicroFIXRequest externalPlaceOrderRequest) {
        List<IMicroFIXResponse> responses = new ArrayList<>();
        Side side = externalPlaceOrderRequest.getSide() == MicroFIXSide.BUY ? Side.BUY : Side.SELL;
        IPlaceOrderRequest placeOrderRequest = orderRequestFactory.createPlaceOrderRequest(
                externalPlaceOrderRequest.getUserID(),
                externalPlaceOrderRequest.getPrice(),
                side,
                externalPlaceOrderRequest.getVolume());
        IMicroFIXResponse externalAckResponse = externalResponseFactory.getReceivedPlaceOrderAckResponse(
                placeOrderRequest.getBookID(),
                placeOrderRequest.getUserID(),
                placeOrderRequest.getOrderID(),
                placeOrderRequest.getPrice(),
                placeOrderRequest.getSide() == Side.BUY ? MicroFIXSide.BUY : MicroFIXSide.SELL,
                placeOrderRequest.getTotalVolume(),
                externalPlaceOrderRequest.getExternalTimestamp());
        responses.add(externalAckResponse);
        //responses.addAll(orderBook.placeOrder(placeOrderRequest));
        orderBook.placeOrder(placeOrderRequest);
        traderAgent.registerResponses(responses);
    }

    private void sendCancelOrderRequest(IMicroFIXRequest externalCancelOrderRequest) {
        List<IMicroFIXResponse> responses = new ArrayList<>();
        ICancelOrderRequest cancelOrderRequest = orderRequestFactory.createCancelOrderRequest(externalCancelOrderRequest.getUserID(),
                externalCancelOrderRequest.getOrderID());
        IMicroFIXResponse externalAckResponse = externalResponseFactory.getReceivedCancelOrderAckResponse(
                cancelOrderRequest.getBookID(),
                cancelOrderRequest.getUserID(),
                cancelOrderRequest.getOrderID(),
                externalCancelOrderRequest.getExternalTimestamp());
        responses.add(externalAckResponse);
        //responses.add(orderBook.cancelOrder(cancelOrderRequest));
        orderBook.cancelOrder(cancelOrderRequest);
        traderAgent.registerResponses(responses);
    }

}
