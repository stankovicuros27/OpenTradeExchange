package api.messages.internal.responses;

public interface ICancelOrderAckResponse extends IResponse {
    public int getUserID();
    public int getOrderID();
}
