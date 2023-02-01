package networking.messages.requests;

import networking.messages.INetworkMessage;

import java.io.Serializable;

public interface INetworkRequest extends INetworkMessage, Serializable {
    public NetworkRequestType getNetworkRequestType();
}
