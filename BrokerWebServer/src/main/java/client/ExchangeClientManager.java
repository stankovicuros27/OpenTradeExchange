package client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class ExchangeClientManager {

    private static final Logger LOGGER = LogManager.getLogger(ExchangeClientManager.class);

    // TODO read client config from properties
    private static final String EXCHANGE_SERVER_IP = "127.0.0.1";
    private static final int EXCHANGE_SERVER_SOCKET = 9999;

    public void initialize() throws IOException {
        LOGGER.info("Initialize");
        ExchangeClientConnection.initializeConnection(EXCHANGE_SERVER_IP, EXCHANGE_SERVER_SOCKET);
    }

    public void close() throws IOException {
        LOGGER.info("Close");
        ExchangeClientConnection.closeConnection();
    }

}
