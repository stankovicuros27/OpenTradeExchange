package api.messages.external;

public interface IExternalCancelOrderRequest extends IExternalRequest {
    public String getBookID();
    public int getUserID();
    public int getOrderID();
}
