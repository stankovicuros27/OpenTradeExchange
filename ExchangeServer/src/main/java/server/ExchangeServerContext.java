package server;

import api.core.IMatchingEngineConfiguration;
import impl.core.MatchingEngine;

public class ExchangeServerContext {

    private static ExchangeServerContext instance = null;

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

}
