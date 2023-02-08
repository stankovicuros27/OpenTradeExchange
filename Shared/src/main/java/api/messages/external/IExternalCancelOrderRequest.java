package api.messages.external;

public interface IExternalCancelOrderRequest extends IExternalRequest {
    public int getUserID();
    public int getOrderID();
}
