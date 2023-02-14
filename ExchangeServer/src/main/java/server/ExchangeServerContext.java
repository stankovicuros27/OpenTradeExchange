package server;

import api.core.IMatchingEngineConfiguration;
import impl.core.MatchingEngine;

public class ExchangeServerContext {

    private static ExchangeServerContext instance = null;

    private final MatchingEngine matchingEngine;
    private final int tcpExchangePort;
    private final String multicastIp;
    private final int l1DataTcpPort;
    private final int l1DataMulticastPort;
    private final int l1TimeoutMS;
    private final int l2DataTcpPort;
    private final int l2DataMulticastPort;
    private final int l2TimeoutMS;
    private final boolean multicastEnabled;
    private final boolean analyticsEnabled;

    public static void initialize(
            IMatchingEngineConfiguration matchingEngineConfiguration,
            int tcpExchangePort,
            String multicastIp,
            int l1DataTcpPort,
            int l1DataMulticastPort,
            int l1TimeoutMS,
            int l2DataTcpPort,
            int l2DataMulticastPort,
            int l2TimeoutMS,
            boolean multicastEnabled,
            boolean analyticsEnabled
    ) {
        if (instance != null) {
            throw new IllegalStateException();
        }
        instance = new ExchangeServerContext(
                matchingEngineConfiguration,
                tcpExchangePort,
                multicastIp,
                l1DataTcpPort,
                l1DataMulticastPort,
                l1TimeoutMS,
                l2DataTcpPort,
                l2DataMulticastPort,
                l2TimeoutMS,
                multicastEnabled,
                analyticsEnabled);
    }

    public static ExchangeServerContext getInstance() {
        if (instance == null) {
            throw new IllegalStateException();
        }
        return instance;
    }

    private ExchangeServerContext(IMatchingEngineConfiguration matchingEngineConfiguration, int tcpExchangePort, String multicastIp, int l1DataTcpPort, int l1DataMulticastPort, int l1TimeoutMS, int l2DataTcpPort, int l2DataMulticastPort, int l2TimeoutMS, boolean multicastEnabled, boolean analyticsEnabled) {
        matchingEngine = new MatchingEngine(matchingEngineConfiguration);
        this.tcpExchangePort = tcpExchangePort;
        this.multicastIp = multicastIp;
        this.l1DataTcpPort = l1DataTcpPort;
        this.l1DataMulticastPort = l1DataMulticastPort;
        this.l1TimeoutMS = l1TimeoutMS;
        this.l2DataTcpPort = l2DataTcpPort;
        this.l2DataMulticastPort = l2DataMulticastPort;
        this.l2TimeoutMS = l2TimeoutMS;
        this.multicastEnabled = multicastEnabled;
        this.analyticsEnabled = analyticsEnabled;
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

    public int getL1DataTcpPort() {
        return l1DataTcpPort;
    }

    public int getL1DataMulticastPort() {
        return l1DataMulticastPort;
    }

    public int getL1DataTimeoutMs() {
        return l1TimeoutMS;
    }

    public int getL2DataTcpPort() {
        return l2DataTcpPort;
    }

    public int getL2DataMulticastPort() {
        return l2DataMulticastPort;
    }

    public int getL2DataTimeoutMs() {
        return l2TimeoutMS;
    }

    public boolean isMulticastEnabled() {
        return multicastEnabled;
    }

    public boolean isAnalyticsEnabled() {
        return analyticsEnabled;
    }

}
