package server.broadcast;

import networking.messages.INetworkMessage;
import server.BrokerConnectionHandler;

import java.util.ArrayList;
import java.util.List;

public abstract class BroadcastService {

    private final List<BrokerConnectionHandler> brokerConnectionHandlers = new ArrayList<>();

    public void registerBrokerConnectionHandler(BrokerConnectionHandler brokerConnectionHandler) {
        brokerConnectionHandlers.add(brokerConnectionHandler);
    }

    public void broadcastMessages(List<INetworkMessage> messages) {
        for (BrokerConnectionHandler handler : brokerConnectionHandlers) {
            if (!handler.isClosed()) {
                for (INetworkMessage message : messages) {
                    handler.sendMessage(message);
                }
            }
        }
    }

}
