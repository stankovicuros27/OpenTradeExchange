package trader.agents;

import api.messages.data.IMicroFIXL1DataMessage;
import api.messages.trading.request.IMicroFIXRequest;
import api.messages.trading.response.IMicroFIXResponse;

import java.util.List;

public interface ITraderAgent {
    public int getUserID();
    public IMicroFIXRequest getNextRequest();
    public void registerResponses(List<IMicroFIXResponse> messages);
    public void registerResponse(IMicroFIXResponse message);
    public void registerL1MarketDataUpdate(IMicroFIXL1DataMessage microFIXL1DataMessage);
}
