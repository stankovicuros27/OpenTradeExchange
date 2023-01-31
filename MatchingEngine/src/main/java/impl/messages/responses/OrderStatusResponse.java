package impl.messages.responses;

import api.messages.responses.IOrderStatusResponse;
import api.messages.responses.OrderResponseStatus;

import java.util.Objects;

public class OrderStatusResponse implements IOrderStatusResponse {

    private final int userID;
    private final int orderID;
    private final OrderResponseStatus status;

    public OrderStatusResponse(int userID, int orderID, OrderResponseStatus status) {
        this.userID = userID;
        this.orderID = orderID;
        this.status = status;
    }

    @Override
    public int getUserID() {
        return userID;
    }

    @Override
    public int getOrderID() {
        return orderID;
    }

    @Override
    public OrderResponseStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderStatusResponse that = (OrderStatusResponse) o;
        return userID == that.userID && orderID == that.orderID && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID, orderID, status);
    }

    @Override
    public String toString() {
        return "StatusResponse{" +
                " userID = " + userID +
                ", orderID = " + orderID +
                ", status = " + status +
                " }";
    }
}
