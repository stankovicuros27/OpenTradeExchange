package server.broadcast;

import api.messages.IMessage;
import server.BrokerConnectionHandler;

import java.util.ArrayList;
import java.util.List;

public abstract class BroadcastService {

    private final List<BrokerConnectionHandler> brokerConnectionHandlers = new ArrayList<>();

    public void registerBrokerConnectionHandler(BrokerConnectionHandler brokerConnectionHandler) {
        brokerConnectionHandlers.add(brokerConnectionHandler);
    }

    public void broadcastMessages(IMessage message) {
        for (BrokerConnectionHandler handler : brokerConnectionHandlers) {
            if (!handler.isClosed()) {
                handler.sendMessage(message);
            }
        }
    }

}
