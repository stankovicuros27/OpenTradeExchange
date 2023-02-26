package server;

import api.core.IMatchingEngineConfiguration;
import impl.core.MatchingEngineConfiguration;
import impl.core.OrderBookConfiguration;

import java.util.Properties;

public class ExchangeServerConfigPropertiesReader {

    private static final String MATCHING_ENGINE_ORDERBOOKS_KEY = "matchingEngine.orderBooks";
    private static final String EXCHANGE_TCP_PORT_KEY = "exchangeTcpPort";
    private static final String EXCHANGE_MULTICAST_IP_KEY = "exchangeMulticastIp";
    private static final String L1_DATA_TCP_PORT_KEY = "l1DataTCPPort";
    private static final String L1_DATA_MULTICAST_PORT_KEY = "l1DataMulticastPort";
    private static final String L1_TIMEOUT_MS_KEY = "l1TimeoutMS";
    private static final String L2_DATA_TCP_PORT_KEY = "l2DataTCPPort";
    private static final String L2_DATA_MULTICAST_PORT_KEY = "l2DataMulticastPort";
    private static final String L2_TIMEOUT_MS_KEY = "l2TimeoutMS";
    private static final String MULTICAST_ENABLED_KEY = "multicastEnabled";
    private static final String EXCHANGE_ANALYTICS_ENABLED = "analyticsEnabled";
    private static final String AUTHENTICATION_DB_ENABLED = "authenticationDbEnabled";
    private static final String TRADING_DB_ENABLED = "tradingDbEnabled";



    private final Properties properties;

    public ExchangeServerConfigPropertiesReader(Properties properties) {
        this.properties = properties;
    }

    public IMatchingEngineConfiguration getMatchingEngineConfiguration() {
        IMatchingEngineConfiguration matchingEngineConfiguration = new MatchingEngineConfiguration();
        String matchingEngineOrderbooks = properties.getProperty(MATCHING_ENGINE_ORDERBOOKS_KEY);
        String[] orderBookStrings = matchingEngineOrderbooks.split(";");
        for (String orderBookString : orderBookStrings) {
            String[] orderBooksSubstrings = orderBookString.split(":");
            String bookID = orderBooksSubstrings[0];
            int decimalPlaces = Integer.parseInt(orderBooksSubstrings[1]);
            matchingEngineConfiguration.registerOrderBookConfiguration(new OrderBookConfiguration(bookID, decimalPlaces));
        }
        return matchingEngineConfiguration;
    }

    public int getExchangeTCPPort() {
        return Integer.parseInt(properties.getProperty(EXCHANGE_TCP_PORT_KEY));
    }

    public String getExchangeMulticastIp() {
        return properties.getProperty(EXCHANGE_MULTICAST_IP_KEY);
    }

    public int getL1DataTCPPort() {
        return Integer.parseInt(properties.getProperty(L1_DATA_TCP_PORT_KEY));
    }

    public int getL1DataMulticastPort() {
        return Integer.parseInt(properties.getProperty(L1_DATA_MULTICAST_PORT_KEY));
    }

    public int getL1TimeoutMs() {
        return Integer.parseInt(properties.getProperty(L1_TIMEOUT_MS_KEY));
    }

    public int getL2DataTCPPort() {
        return Integer.parseInt(properties.getProperty(L2_DATA_TCP_PORT_KEY));
    }

    public int getL2DataMulticastPort() {
        return Integer.parseInt(properties.getProperty(L2_DATA_MULTICAST_PORT_KEY));
    }

    public int getL2TimeoutMs() {
        return Integer.parseInt(properties.getProperty(L2_TIMEOUT_MS_KEY));
    }

    public boolean isMulticastEnabled() {
        return Boolean.parseBoolean(properties.getProperty(MULTICAST_ENABLED_KEY));
    }

    public boolean isAnalyticsEnabled() {
        return Boolean.parseBoolean(properties.getProperty(EXCHANGE_ANALYTICS_ENABLED));
    }

    public boolean isAuthenticationDbEnabled() {
        return Boolean.parseBoolean(properties.getProperty(AUTHENTICATION_DB_ENABLED));
    }

    public boolean isTradingDbEnabled() {
        return Boolean.parseBoolean(properties.getProperty(TRADING_DB_ENABLED));
    }

}
