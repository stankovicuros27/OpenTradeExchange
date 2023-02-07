package api.messages.internal.requests;

import api.messages.internal.IMessage;

public interface IRequest extends IMessage {
    public RequestType getRequestType();
}
