package trader.runners;

import api.core.Side;
import api.messages.external.ExternalSide;
import api.messages.external.request.ExternalRequestType;
import api.messages.external.request.IExternalRequest;
import api.messages.external.response.IExternalResponse;
import api.messages.external.response.IExternalResponseFactory;
import impl.messages.external.response.ExternalResponseFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import trader.agents.ITraderAgent;
import api.core.IOrderBook;
import api.messages.requests.ICancelOrderRequest;
import api.messages.requests.IPlaceOrderRequest;
import api.core.IOrderRequestFactory;

import java.util.ArrayList;
import java.util.List;

public class LocalTraderAgentRunner implements ITraderAgentRunner {

    private static final Logger LOGGER = LogManager.getLogger(LocalTraderAgentRunner.class);

    private final ITraderAgent traderAgent;
    private final IOrderBook orderBook;
    private final IOrderRequestFactory orderRequestFactory;
    private final IExternalResponseFactory externalResponseFactory = new ExternalResponseFactory();

    public LocalTraderAgentRunner(ITraderAgent traderAgent, IOrderBook orderBook) {
        this.traderAgent = traderAgent;
        this.orderBook = orderBook;
        this.orderRequestFactory = orderBook.getOrderRequestFactory();
    }

    @Override
    public void run() {
        LOGGER.info("Starting LocalTradeAgentRunner for OrderBook: " + orderBook.getBookID());
        while(true) {
            IExternalRequest externalRequest = traderAgent.getNextRequest();
            if (externalRequest.getExternalRequestType() == ExternalRequestType.PLACE) {
                sendPlaceOrderRequest(externalRequest);
            } else {
                sendCancelOrderRequest(externalRequest);
            }
            try {
                Thread.sleep((long) (Math.random() * SLEEP_TIME_MS));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void sendPlaceOrderRequest(IExternalRequest externalPlaceOrderRequest) {
        List<IExternalResponse> responses = new ArrayList<>();
        Side side = externalPlaceOrderRequest.getSide() == ExternalSide.BUY ? Side.BUY : Side.SELL;
        IPlaceOrderRequest placeOrderRequest = orderRequestFactory.createPlaceOrderRequest(
                externalPlaceOrderRequest.getUserID(),
                externalPlaceOrderRequest.getPrice(),
                side,
                externalPlaceOrderRequest.getVolume());
        IExternalResponse externalAckResponse = externalResponseFactory.getReceivedPlaceOrderAckResponse(
                externalPlaceOrderRequest.getBookID(),
                externalPlaceOrderRequest.getUserID(),
                externalPlaceOrderRequest.getPrice(),
                externalPlaceOrderRequest.getSide(),
                externalPlaceOrderRequest.getVolume(),
                externalPlaceOrderRequest.getExternalTimestamp());
        responses.add(externalAckResponse);
        //responses.addAll(orderBook.placeOrder(placeOrderRequest));
        orderBook.placeOrder(placeOrderRequest);
        traderAgent.registerResponses(responses);
    }

    private void sendCancelOrderRequest(IExternalRequest externalCancelOrderRequest) {
        List<IExternalResponse> responses = new ArrayList<>();
        ICancelOrderRequest cancelOrderRequest = orderRequestFactory.createCancelOrderRequest(externalCancelOrderRequest.getUserID(),
                externalCancelOrderRequest.getOrderID());
        IExternalResponse externalAckResponse = externalResponseFactory.getReceivedCancelOrderAckResponse(
                externalCancelOrderRequest.getBookID(),
                externalCancelOrderRequest.getUserID(),
                externalCancelOrderRequest.getOrderID(),
                externalCancelOrderRequest.getPrice(),
                externalCancelOrderRequest.getSide(),
                externalCancelOrderRequest.getVolume(),
                externalCancelOrderRequest.getExternalTimestamp());
        responses.add(externalAckResponse);
        //responses.add(orderBook.cancelOrder(cancelOrderRequest));
        orderBook.cancelOrder(cancelOrderRequest);
        traderAgent.registerResponses(responses);
    }

}
