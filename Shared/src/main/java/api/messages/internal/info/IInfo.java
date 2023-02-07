package api.messages.internal.info;

import api.messages.IMessage;
import api.messages.MessageType;

public interface IInfo extends IMessage {
    @Override
    default MessageType getMessageType() {
        return MessageType.INFO;
    }
}
