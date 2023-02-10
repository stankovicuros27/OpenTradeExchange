package api.messages.responses;

public interface ICancelOrderAckResponse extends IResponse {
    public int getUserID();
    public int getOrderID();
}
