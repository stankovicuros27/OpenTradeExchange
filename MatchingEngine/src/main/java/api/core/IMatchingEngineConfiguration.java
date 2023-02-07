package api.core;

import java.util.List;

public interface IMatchingEngineConfiguration {
    public void registerOrderBookConfiguration(IOrderBookConfiguration orderBookConfiguration);
    public List<IOrderBookConfiguration> getOrderBookConfigurations();
}
