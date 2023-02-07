package trader.agents;

import trader.agents.messages.ITraderRequestInfo;
import api.messages.responses.IResponse;

import java.util.List;

public interface ITraderAgent {
    public ITraderRequestInfo getNextRequest();
    public void registerResponses(List<IResponse> responses);
}
