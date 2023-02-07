package trader.agents;

import api.messages.external.IExternalRequest;
import api.messages.internal.responses.IResponse;

import java.util.List;

public interface ITraderAgent {
    public IExternalRequest getNextRequest();
    public void registerResponses(List<IResponse> responses);
}
