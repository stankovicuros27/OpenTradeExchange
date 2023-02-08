package trader.runners;

import api.messages.IMessage;
import api.messages.external.ExternalRequestType;
import api.messages.external.IExternalCancelOrderRequest;
import api.messages.external.IExternalPlaceOrderRequest;
import api.messages.external.IExternalRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import trader.agents.ITraderAgent;
import api.core.IOrderBook;
import api.messages.internal.requests.ICancelOrderRequest;
import api.messages.internal.requests.IPlaceOrderRequest;
import api.messages.internal.responses.ICancelOrderAckResponse;
import api.messages.internal.responses.IPlaceOrderAckResponse;
import api.core.IOrderRequestFactory;

import java.util.ArrayList;
import java.util.List;

public class LocalTraderAgentRunner implements ITraderAgentRunner {

    private static final Logger LOGGER = LogManager.getLogger(LocalTraderAgentRunner.class);

    private final ITraderAgent traderAgent;
    private final IOrderBook orderBook;
    private final IOrderRequestFactory orderRequestFactory;

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
                IExternalPlaceOrderRequest externalPlaceOrderRequest = (IExternalPlaceOrderRequest) externalRequest;
                sendPlaceOrderRequest(externalPlaceOrderRequest);
            } else {
                IExternalCancelOrderRequest externalCancelOrderRequest = (IExternalCancelOrderRequest) externalRequest;
                sendCancelOrderRequest(externalCancelOrderRequest);
            }
            try {
                Thread.sleep((long) (Math.random() * SLEEP_TIME_MS));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void sendPlaceOrderRequest(IExternalPlaceOrderRequest externalPlaceOrderRequest) {
        List<IMessage> responses = new ArrayList<>();
        IPlaceOrderRequest placeOrderRequest = orderRequestFactory.createPlaceOrderRequest(externalPlaceOrderRequest.getUserID(),
                externalPlaceOrderRequest.getPrice(),
                externalPlaceOrderRequest.getSide(),
                externalPlaceOrderRequest.getVolume());
        IPlaceOrderAckResponse placeOrderAckResponse = orderRequestFactory.createPlaceOrderAckResponse(placeOrderRequest, externalPlaceOrderRequest.getTimestamp());
        responses.add(placeOrderAckResponse);
        responses.addAll(orderBook.placeOrder(placeOrderRequest));
        traderAgent.registerMessages(responses);
    }

    private void sendCancelOrderRequest(IExternalCancelOrderRequest externalCancelOrderRequest) {
        List<IMessage> responses = new ArrayList<>();
        ICancelOrderRequest cancelOrderRequest = orderRequestFactory.createCancelOrderRequest(externalCancelOrderRequest.getUserID(),
                externalCancelOrderRequest.getOrderID());
        ICancelOrderAckResponse cancelOrderAckResponse = orderRequestFactory.createCancelOrderAckResponse(cancelOrderRequest, externalCancelOrderRequest.getTimestamp());
        responses.add(cancelOrderAckResponse);
        responses.add(orderBook.cancelOrder(cancelOrderRequest));
        traderAgent.registerMessages(responses);
    }

}
