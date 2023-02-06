package server;

import server.direct.ExchangeServerManager;

public class ExchangeServerInitializator {

    public static void initialize() {
        ExchangeServerContext.initialize();
        ExchangeServerManager exchangeServerManager = new ExchangeServerManager(ExchangeServerContext.getInstance());
        exchangeServerManager.startDirectExchangeServer();
    }

}
