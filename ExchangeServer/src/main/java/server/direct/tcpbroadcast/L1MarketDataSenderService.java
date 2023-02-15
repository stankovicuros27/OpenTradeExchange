package server.direct.tcpbroadcast;

import api.messages.data.IMicroFIXL1DataMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.direct.core.ExchangeResponseSenderService;

import java.util.HashMap;
import java.util.Map;

public class L1MarketDataSenderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(L1MarketDataSenderService.class);

    private final Map<Integer, L1MarketDataSubscriptionHandler> subscriptionHandlers = new HashMap<>();

    public synchronized void registerSubscriptionHandler(L1MarketDataSubscriptionHandler l1MarketDataSubscriptionHandler) {
        if (!subscriptionHandlers.containsKey(l1MarketDataSubscriptionHandler.getUserID())) {
            LOGGER.info("Register L1 Data SubscriptionHandler at IP address: " + l1MarketDataSubscriptionHandler.getClientIpAddress() + ", with userID: " + l1MarketDataSubscriptionHandler.getUserID());
            subscriptionHandlers.put(l1MarketDataSubscriptionHandler.getUserID(), l1MarketDataSubscriptionHandler);
        }
    }

    public synchronized void removeSubscriptionHandler(L1MarketDataSubscriptionHandler l1MarketDataSubscriptionHandler) {
        if (subscriptionHandlers.containsKey(l1MarketDataSubscriptionHandler.getUserID())) {
            LOGGER.info("Remove L1 Data SubscriptionHandler at IP address: " + l1MarketDataSubscriptionHandler.getClientIpAddress() + ", with userID: " + l1MarketDataSubscriptionHandler.getUserID());
            subscriptionHandlers.remove(l1MarketDataSubscriptionHandler.getUserID());
        }
    }

    public synchronized void distributeMessage(IMicroFIXL1DataMessage iMicroFIXL1DataMessage) {
        for (L1MarketDataSubscriptionHandler subscriptionHandler : subscriptionHandlers.values()) {
            subscriptionHandler.sendMessage(iMicroFIXL1DataMessage);
        }
    }

}
