package networking.messages.requests;

import java.io.Serializable;

public interface INetworkRequest extends Serializable {
    public NetworkRequestType getNetworkRequestType();
}
