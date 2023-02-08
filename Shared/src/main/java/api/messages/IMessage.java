package api.messages;

import java.io.Serializable;

public interface IMessage extends Serializable {
    public String getBookID();
    public int getTimestamp();
    public MessageType getMessageType();
}
