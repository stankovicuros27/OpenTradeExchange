package impl.core;

import api.core.IMatchingEngineConfiguration;
import api.core.IOrderBookConfiguration;

import java.util.ArrayList;
import java.util.List;

public class MatchingEngineConfiguration implements IMatchingEngineConfiguration {

    private final List<IOrderBookConfiguration> orderBookConfigurations = new ArrayList<>();

    @Override
    public void registerOrderBookConfiguration(IOrderBookConfiguration orderBookConfiguration) {
        orderBookConfigurations.add(orderBookConfiguration);
    }

    @Override
    public List<IOrderBookConfiguration> getOrderBookConfigurations() {
        return orderBookConfigurations;
    }

}
