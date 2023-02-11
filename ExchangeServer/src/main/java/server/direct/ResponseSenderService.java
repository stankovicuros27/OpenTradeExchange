package server.direct;

import api.messages.trading.response.IMicroFIXResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseSenderService {

    private static final Logger LOGGER = LogManager.getLogger(ResponseSenderService.class);

    private final Map<Integer, ConnectionHandler> connectionHandlers = new HashMap<>();

    public synchronized void registerConnectionHandler(ConnectionHandler connectionHandler) {
        LOGGER.info("Register ConnectionHandler at IP address: " + connectionHandler.getClientIpAddress() + ", with userID: " + connectionHandler.getUserID());
        connectionHandlers.put(connectionHandler.getUserID(), connectionHandler);
    }

    public synchronized void removeConnectionHandler(ConnectionHandler connectionHandler) {
        LOGGER.info("Remove ConnectionHandler at IP address: " + connectionHandler.getClientIpAddress() + ", with userID: " + connectionHandler.getUserID());
        connectionHandlers.remove(connectionHandler.getUserID());
    }

    public synchronized void distributeMessages(List<IMicroFIXResponse> microFIXResponses) {
        for (IMicroFIXResponse microFIXResponse : microFIXResponses) {
            connectionHandlers.get(microFIXResponse.getUserID()).sendMessage(microFIXResponse);
        }
    }

}
