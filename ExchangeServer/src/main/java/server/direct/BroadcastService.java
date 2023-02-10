package server.direct;

import api.messages.external.response.IExternalResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class BroadcastService {

    private static final Logger LOGGER = LogManager.getLogger(BroadcastService.class);

    private final List<ConnectionHandler> connectionHandlers = new ArrayList<>();

    public synchronized void registerBrokerConnectionHandler(ConnectionHandler connectionHandler) {
        LOGGER.info("Register BrokerConnectionHandler at IP address: " + connectionHandler.getClientIpAddress());
        connectionHandlers.add(connectionHandler);
    }

    public synchronized void removeBrokerConnectionHandler(ConnectionHandler connectionHandler) {
        LOGGER.info("Remove BrokerConnectionHandler at IP address: " + connectionHandler.getClientIpAddress());
        connectionHandlers.remove(connectionHandler);
    }

    public synchronized void broadcastMessages(List<IExternalResponse> externalResponses) {
        for (ConnectionHandler handler : connectionHandlers) {
            for (IExternalResponse externalResponse : externalResponses) {
                handler.sendMessage(externalResponse);
            }
        }
    }

}
