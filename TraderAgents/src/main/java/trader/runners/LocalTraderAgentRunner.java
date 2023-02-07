package trader.runners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import trader.TraderAgentManager;
import trader.agents.ITraderAgent;
import trader.agents.messages.CancelOrderRequestInfo;
import trader.agents.messages.ITraderRequestInfo;
import trader.agents.messages.PlaceOrderRequestInfo;
import trader.agents.messages.TraderRequestInfoType;
import api.core.IMatchingEngine;
import api.core.IOrderBook;
import api.messages.requests.ICancelOrderRequest;
import api.messages.requests.IPlaceOrderRequest;
import api.messages.responses.ICancelOrderAckResponse;
import api.messages.responses.IPlaceOrderAckResponse;
import api.messages.responses.IResponse;
import api.messages.util.IOrderRequestFactory;

import java.util.ArrayList;
import java.util.List;

public class LocalTraderAgentRunner implements ITraderAgentRunner {

    private static final Logger LOGGER = LogManager.getLogger(LocalTraderAgentRunner.class);


    private final ITraderAgent traderAgent;
    private final IOrderRequestFactory orderRequestFactory;
    private final IOrderBook orderBook;

    public LocalTraderAgentRunner(ITraderAgent traderAgent, IMatchingEngine matchingEngine) {
        this.traderAgent = traderAgent;
        orderRequestFactory = matchingEngine.getOrderRequestFactory();
        orderBook = matchingEngine.getOrderBook();
    }

    @Override
    public void run() {
        LOGGER.info("Starting LocalTradeAgentRunner");
        while(true) {
            ITraderRequestInfo traderRequestInfo = traderAgent.getNextRequest();
            if (traderRequestInfo.getType() == TraderRequestInfoType.PLACE) {
                PlaceOrderRequestInfo placeOrderRequestInfo = (PlaceOrderRequestInfo) traderRequestInfo;
                sendPlaceOrderRequest(placeOrderRequestInfo);
            } else {
                CancelOrderRequestInfo cancelOrderRequestInfo = (CancelOrderRequestInfo) traderRequestInfo;
                sendCancelOrderRequest(cancelOrderRequestInfo);
            }
        }
    }

    private void sendPlaceOrderRequest(PlaceOrderRequestInfo placeOrderRequestInfo) {
        List<IResponse> responses = new ArrayList<>();
        IPlaceOrderRequest placeOrderRequest = orderRequestFactory.createPlaceOrderRequest(placeOrderRequestInfo.getUserID(),
                placeOrderRequestInfo.getPrice(),
                placeOrderRequestInfo.getSide(),
                placeOrderRequestInfo.getVolume());
        IPlaceOrderAckResponse placeOrderAckResponse = orderRequestFactory.createPlaceOrderAckResponse(placeOrderRequest);
        responses.add(placeOrderAckResponse);
        orderBook.placeOrder(placeOrderRequest);
        //responses.addAll(orderBook.placeOrder(placeOrderRequest));
        traderAgent.registerResponses(responses);
    }

    private void sendCancelOrderRequest(CancelOrderRequestInfo cancelOrderRequestInfo) {
        List<IResponse> responses = new ArrayList<>();
        ICancelOrderRequest cancelOrderRequest = orderRequestFactory.createCancelOrderRequest(cancelOrderRequestInfo.getUserID(),
                cancelOrderRequestInfo.getOrderID());
        ICancelOrderAckResponse cancelOrderAckResponse = orderRequestFactory.createCancelOrderAckResponse(cancelOrderRequest);
        responses.add(cancelOrderAckResponse);
        orderBook.cancelOrder(cancelOrderRequest);
        //responses.add(orderBook.cancelOrder(cancelOrderRequest));
        traderAgent.registerResponses(responses);
    }

}
