package api.messages.internal.responses;

import api.messages.internal.IMessage;

public interface IResponse extends IMessage {
    public ResponseType getType();
}
