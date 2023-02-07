package trader.agents;

import api.messages.external.IExternalRequest;
import api.messages.IMessage;

import java.util.List;

public interface ITraderAgent {
    public IExternalRequest getNextRequest();
    public void registerMessages(List<IMessage> messages);
    public void registerMessage(IMessage message);
}
