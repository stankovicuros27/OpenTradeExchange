package trader.runners;

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

import java.util.ArrayList;
import java.util.List;

public class LocalTraderAgentRunner implements ITraderAgentRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalTraderAgentRunner.class);

    private final ITraderAgent traderAgent;
    private final IOrderBook orderBook;
    private final IOrderRequestFactory orderRequestFactory;
    private final IMicroFIXResponseFactory externalResponseFactory = new MicroFIXResponseFactory();

    public LocalTraderAgentRunner(ITraderAgent traderAgent, IOrderBook orderBook) {
        this.traderAgent = traderAgent;
        this.orderBook = orderBook;
        this.orderRequestFactory = orderBook.getOrderRequestFactory();
    }

    @Override
    public void run() {
        LOGGER.info("Starting LocalTradeAgentRunner for OrderBook: " + orderBook.getBookID());
        while(true) {
            IMicroFIXRequest externalRequest = traderAgent.getNextRequest();
            if (externalRequest.getExternalRequestType() == MicroFIXRequestType.PLACE) {
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
