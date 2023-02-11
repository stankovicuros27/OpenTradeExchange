package api.messages.trading.request;

import api.messages.trading.IMicroFIXMessage;

public interface IMicroFIXRequest extends IMicroFIXMessage {
    public MicroFIXRequestType getExternalRequestType();
}
