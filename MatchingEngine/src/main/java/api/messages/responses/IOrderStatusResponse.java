package api.messages.responses;

public interface IOrderStatusResponse extends IResponse {
    public int getUserID();
    public int getOrderID();
    public OrderResponseStatus getStatus();
}
