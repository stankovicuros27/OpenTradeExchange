package api.messages.internal.requests;

import api.messages.IMessage;
import api.messages.MessageType;

public interface IRequest extends IMessage {
    public RequestType getRequestType();

    @Override
    default MessageType getMessageType() {
        return MessageType.REQUEST;
    }
}
