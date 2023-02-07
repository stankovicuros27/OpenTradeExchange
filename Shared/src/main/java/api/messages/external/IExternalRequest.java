package api.messages.external;

import api.messages.internal.IMessage;

public interface IExternalRequest extends IMessage {
    public ExternalRequestType getExternalRequestType();
}
