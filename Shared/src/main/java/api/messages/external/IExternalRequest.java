package api.messages.external;

import api.messages.IMessage;
import api.messages.MessageType;

public interface IExternalRequest extends IMessage {
    public ExternalRequestType getExternalRequestType();

    @Override
    default MessageType getMessageType() {
        return MessageType.EXTERNAL_REQUEST;
    }
}
