package server;

import impl.core.MatchingEngine;

public class ExchangeServerContext {

    private static ExchangeServerContext instance = null;

    private final MatchingEngine matchingEngine;

    public static void initialize() {
        if (instance != null) {
            throw new IllegalStateException();
        }
        instance = new ExchangeServerContext();
    }

    public static ExchangeServerContext getInstance() {
        if (instance == null) {
            throw new IllegalStateException();
        }
        return instance;
    }

    private ExchangeServerContext() {
        matchingEngine = new MatchingEngine();
    }

    public MatchingEngine getMatchingEngine() {
        return matchingEngine;
    }

}
