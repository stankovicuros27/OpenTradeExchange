package api.messages.trading.response;

import api.messages.trading.IMicroFIXMessage;

public interface IMicroFIXResponse extends IMicroFIXMessage {
    public MicroFIXResponseType getExternalResponseType();
}
