package api.messages.responses;

import api.messages.IMessage;

public interface IOrderStatusResponse extends IResponse {
    public int getUserID();
    public int getOrderID();
    public OrderResponseStatus getStatus();
}
