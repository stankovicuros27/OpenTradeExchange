package server;

import api.core.IOrderBook;
import server.direct.ExchangeServerManager;
import trader.agents.ITraderAgentManager;
import trader.agents.controlled.ControlledTraderAgentManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExchangeServerInitializer {

    private static final String SERVER_CONFIG_PROPERTIES_PATH = "ExchangeServer/server-config.properties";

    public static void initialize() {
        initializeContextFromProperties();
        ExchangeServerManager exchangeServerManager = new ExchangeServerManager(ExchangeServerContext.getInstance());
        exchangeServerManager.startDirectExchangeServer();

        // Start dummy traders
        for (IOrderBook orderBook : ExchangeServerContext.getInstance().getMatchingEngine().getAllOrderBooks()) {
            ExecutorService traderThreadPool = Executors.newCachedThreadPool();
            ITraderAgentManager controllerTraderAgentManager = new ControlledTraderAgentManager(orderBook, traderThreadPool);
            new Thread(controllerTraderAgentManager).start();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void initializeContextFromProperties() {
        try (InputStream input = new FileInputStream(SERVER_CONFIG_PROPERTIES_PATH)) {
            Properties properties = new Properties();
            properties.load(input);
            ExchangeServerConfigPropertiesReader exchangeServerConfigPropertiesReader = new ExchangeServerConfigPropertiesReader(properties);
            ExchangeServerContext.initialize(
                    exchangeServerConfigPropertiesReader.getMatchingEngineConfiguration(),
                    exchangeServerConfigPropertiesReader.getExchangeTCPPort(),
                    exchangeServerConfigPropertiesReader.getExchangeMulticastIp(),
                    exchangeServerConfigPropertiesReader.getL1DataMulticastPort(),
                    exchangeServerConfigPropertiesReader.getL1TimeoutMs(),
                    exchangeServerConfigPropertiesReader.getL2DataMulticastPort(),
                    exchangeServerConfigPropertiesReader.getL2TimeoutMs()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
