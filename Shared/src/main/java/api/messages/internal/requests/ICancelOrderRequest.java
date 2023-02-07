package api.messages.internal.requests;

public interface ICancelOrderRequest extends IRequest {
    public int getUserID();
    public int getOrderID();
}
