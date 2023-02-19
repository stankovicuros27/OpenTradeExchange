package server;

import api.core.IOrderBook;
import authenticationdb.AuthenticationDBConnection;
import server.direct.ServerManager;
import trader.agents.ITraderAgentManager;
import trader.agents.controlled.ControlledTraderAgentManager;
import tradingdatadb.TradingDataDBConnection;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExchangeServerInitializer {

    private static final String SERVER_CONFIG_PROPERTIES_PATH = "server-config.properties";

    public static void initialize() {

        try {
            AuthenticationDBConnection.initialize();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        initializeContextFromProperties();
        startExchangeServer();
        
        testMongoDB();

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

    private static void testMongoDB() {
        TradingDataDBConnection.initialize();
        TradingDataDBConnection.getInstance().testDB();
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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
                    exchangeServerConfigPropertiesReader.getL1DataTCPPort(),
                    exchangeServerConfigPropertiesReader.getL1DataMulticastPort(),
                    exchangeServerConfigPropertiesReader.getL1TimeoutMs(),
                    exchangeServerConfigPropertiesReader.getL2DataTCPPort(),
                    exchangeServerConfigPropertiesReader.getL2DataMulticastPort(),
                    exchangeServerConfigPropertiesReader.getL2TimeoutMs(),
                    exchangeServerConfigPropertiesReader.isMulticastEnabled(),
                    exchangeServerConfigPropertiesReader.isAnalyticsEnabled()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void startExchangeServer() {
        ServerManager serverManager = new ServerManager(ExchangeServerContext.getInstance());
        serverManager.startDirectExchangeServer();
    }

}
