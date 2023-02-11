package api.messages.authentication;

import java.io.Serializable;

public interface IMicroFIXAuthenticationMessage extends Serializable {
    public int getUserID();
    public int getTimestamp();
}
