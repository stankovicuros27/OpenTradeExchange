package api.messages.external.response;

import api.messages.external.IExternalMessage;

public interface IExternalResponse extends IExternalMessage {
    public ExternalResponseType getExternalResponseType();
}
