package impl.messages.responses;

import api.messages.responses.IOrderStatusResponse;
import api.messages.responses.OrderResponseStatus;
import api.messages.responses.ResponseType;

import java.util.Objects;

public class OrderStatusResponse implements IOrderStatusResponse {

    private final String bookID;
    private final int userID;
    private final int orderID;
    private final OrderResponseStatus status;
    private final int timestamp;


    public OrderStatusResponse(String bookID, int userID, int orderID, OrderResponseStatus status, int timestamp) {
        this.bookID = bookID;
        this.userID = userID;
        this.orderID = orderID;
        this.status = status;
        this.timestamp = timestamp;
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
    public ResponseType getType() {
        return ResponseType.OrderStatusResponse;
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

    @Override
    public String getBookID() {
        return bookID;
    }

    @Override
    public int getTimestamp() {
        return timestamp;
    }
}
