package server.web;

import api.messages.requests.IRequest;
import api.messages.util.IOrderRequestFactory;
import api.sides.Side;
import api.time.ITimestampProvider;
import impl.messages.util.OrderRequestFactory;
import impl.time.InstantTimestampProvider;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/sendRequest")
public class SendRequestWebServlet extends HttpServlet {

    private final ITimestampProvider timestampProvider = new InstantTimestampProvider();
    private final IOrderRequestFactory orderRequestFactory = new OrderRequestFactory(timestampProvider);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestType = request.getParameter("requestType");
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        IRequest exchangeRequest;
        if (requestType.equals("PLACE_ORDER")) {
            int userID = Integer.parseInt(request.getParameter("userID"));
            double price = Double.parseDouble(request.getParameter("price"));
            Side side = Side.fromString(request.getParameter("side"));
            int volume = Integer.parseInt(request.getParameter("volume"));
            exchangeRequest = orderRequestFactory.createPlaceOrderRequest(userID, price, side, volume);
        } else if (requestType.equals("CANCEL_ORDER")) {
            int userID = Integer.parseInt(request.getParameter("userID"));
            int orderID = Integer.parseInt(request.getParameter("orderID"));
            exchangeRequest = orderRequestFactory.createCancelOrderRequest(userID, orderID);
        } else {
            // TODO handle
            throw new IOException();
        }
        // TODO
    }

}