package server.web;

import api.messages.internal.requests.ICancelOrderRequest;
import api.messages.internal.requests.IPlaceOrderRequest;
import api.messages.internal.util.IOrderRequestFactory;
import api.sides.Side;
import api.time.ITimestampProvider;
import impl.messages.internal.util.OrderRequestFactory;
import impl.time.InstantTimestampProvider;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import server.ExchangeServerContext;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/sendRequest")
public class SendRequestWebServlet extends HttpServlet {

    //private final ITimestampProvider timestampProvider = new InstantTimestampProvider();
    //private final IOrderRequestFactory orderRequestFactory = new OrderRequestFactory(timestampProvider, roundDecimalPlaces);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        /*String requestType = request.getParameter("requestType");
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        if (requestType.equals("PLACE_ORDER")) {
            int userID = Integer.parseInt(request.getParameter("userID"));
            double price = Double.parseDouble(request.getParameter("price"));
            Side side = Side.fromString(request.getParameter("side"));
            int volume = Integer.parseInt(request.getParameter("volume"));
            IPlaceOrderRequest placeOrderRequest = orderRequestFactory.createPlaceOrderRequest(userID, price, side, volume);
            ExchangeServerContext.getInstance().getMatchingEngine().getOrderBook().placeOrder(placeOrderRequest);
        } else if (requestType.equals("CANCEL_ORDER")) {
            int userID = Integer.parseInt(request.getParameter("userID"));
            int orderID = Integer.parseInt(request.getParameter("orderID"));
            ICancelOrderRequest cancelOrderRequest = orderRequestFactory.createCancelOrderRequest(userID, orderID);
            ExchangeServerContext.getInstance().getMatchingEngine().getOrderBook().cancelOrder(cancelOrderRequest);
        } else {
            throw new IOException();
        }*/
    }

}