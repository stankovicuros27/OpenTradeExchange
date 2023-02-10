package api.messages.external.request;

import api.messages.external.IExternalMessage;

public interface IExternalRequest extends IExternalMessage {
    public ExternalRequestType getExternalRequestType();
}
