package api.messages.internal.responses;

public interface IOrderStatusResponse extends IResponse {
    public int getUserID();
    public int getOrderID();
    public OrderResponseStatus getStatus();
}
