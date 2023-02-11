package api.messages.data;

import java.io.Serializable;

public interface IMicroFIXDataMessage extends Serializable {
    public String getBookID();
    public int getTimestamp();
    public int getSizeInBytes();
}
