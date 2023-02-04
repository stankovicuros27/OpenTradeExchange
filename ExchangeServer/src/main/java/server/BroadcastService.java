package server;

import api.messages.IMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class BroadcastService {

    private static final Logger LOGGER = LogManager.getLogger(BroadcastService.class);

    private final List<BrokerConnectionHandler> brokerConnectionHandlers = new ArrayList<>();

    public synchronized void registerBrokerConnectionHandler(BrokerConnectionHandler brokerConnectionHandler) {
        LOGGER.info("Register BrokerConnectionHandler at IP address: " + brokerConnectionHandler.getIpAddress());
        brokerConnectionHandlers.add(brokerConnectionHandler);
    }

    public synchronized void removeBrokerConnectionHandler(BrokerConnectionHandler brokerConnectionHandler) {
        LOGGER.info("Remove BrokerConnectionHandler at IP address: " + brokerConnectionHandler.getIpAddress());
        brokerConnectionHandlers.remove(brokerConnectionHandler);
    }

    public synchronized void broadcastMessages(IMessage message) {
        for (BrokerConnectionHandler handler : brokerConnectionHandlers) {
            handler.sendMessage(message);
        }
    }

}
