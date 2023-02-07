package api.messages.internal.responses;

import api.messages.IMessage;
import api.messages.MessageType;

public interface IResponse extends IMessage {
    public ResponseType getType();

    @Override
    default MessageType getMessageType() {
        return MessageType.RESPONSE;
    }
}
