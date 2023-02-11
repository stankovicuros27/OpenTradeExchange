package server;

import api.core.IMatchingEngineConfiguration;
import impl.core.MatchingEngine;

public class ExchangeServerContext {

    private static ExchangeServerContext instance = null;

    // TODO read from config
    private final int tcpExchangePort = 9999;
    private final String multicastIp = "225.4.5.6";
    private final int l1DataMulticastPort = 9998;
    private final int l1TimeoutMS = 200;
    private final int l2DataMulticastPort = 9997;
    private final int l2TimeoutMS = 1000;

    private final MatchingEngine matchingEngine;

    public static void initialize(IMatchingEngineConfiguration matchingEngineConfiguration) {
        if (instance != null) {
            throw new IllegalStateException();
        }
        instance = new ExchangeServerContext(matchingEngineConfiguration);
    }

    public static ExchangeServerContext getInstance() {
        if (instance == null) {
            throw new IllegalStateException();
        }
        return instance;
    }

    private ExchangeServerContext(IMatchingEngineConfiguration matchingEngineConfiguration) {
        matchingEngine = new MatchingEngine(matchingEngineConfiguration);
    }

    public MatchingEngine getMatchingEngine() {
        return matchingEngine;
    }

    public int getTcpExchangePort() {
        return tcpExchangePort;
    }

    public String getMulticastIp() {
        return multicastIp;
    }

    public int getL1DataMulticastPort() {
        return l1DataMulticastPort;
    }

    public int getL1DataTimeoutMs() {
        return l1TimeoutMS;
    }

    public int getL2DataMulticastPort() {
        return l2DataMulticastPort;
    }

    public int getL2DataTimeoutMs() {
        return l2TimeoutMS;
    }

}
