package api.messages.requests;

public interface ICancelOrderRequest extends IRequest {
    public int getUserID();
    public int getOrderID();
}
