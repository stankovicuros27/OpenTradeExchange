package trader.agents;

import api.messages.external.request.IExternalRequest;
import api.messages.external.response.IExternalResponse;

import java.util.List;

public interface ITraderAgent {
    public IExternalRequest getNextRequest();
    public void registerResponses(List<IExternalResponse> messages);
    public void registerResponse(IExternalResponse message);
}
