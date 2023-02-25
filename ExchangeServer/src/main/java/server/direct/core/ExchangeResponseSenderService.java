package server.direct.core;

import api.messages.trading.response.IMicroFIXResponse;
import api.messages.trading.response.MicroFIXResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tradingdatadb.TradingDataDBConnection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExchangeResponseSenderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeResponseSenderService.class);

    private final Map<Integer, ExchangeConnectionHandler> connectionHandlers = new HashMap<>();

    public synchronized void registerConnectionHandler(ExchangeConnectionHandler exchangeConnectionHandler) {
        if (!connectionHandlers.containsKey(exchangeConnectionHandler.getUserID())) {
            LOGGER.info("Register ConnectionHandler at IP address: " + exchangeConnectionHandler.getClientIpAddress() + ", with userID: " + exchangeConnectionHandler.getUserID());
            connectionHandlers.put(exchangeConnectionHandler.getUserID(), exchangeConnectionHandler);
        }
    }

    public synchronized void removeConnectionHandler(ExchangeConnectionHandler exchangeConnectionHandler) {
        if (connectionHandlers.containsKey(exchangeConnectionHandler.getUserID())) {
            LOGGER.info("Remove ConnectionHandler at IP address: " + exchangeConnectionHandler.getClientIpAddress() + ", with userID: " + exchangeConnectionHandler.getUserID());
            connectionHandlers.remove(exchangeConnectionHandler.getUserID());
        }
    }

    public synchronized void distributeMessages(List<IMicroFIXResponse> microFIXResponses) {
        for (IMicroFIXResponse microFIXResponse : microFIXResponses) {
            if (microFIXResponse.getExternalResponseType() == MicroFIXResponseType.TRADE) {
                TradingDataDBConnection.getInstance().insertTrade(microFIXResponse);
            }
            connectionHandlers.get(microFIXResponse.getUserID()).sendMessage(microFIXResponse);
        }
    }

}
