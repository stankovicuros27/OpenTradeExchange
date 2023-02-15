package server.web.trade;

import api.core.IMatchingEngine;
import api.core.IOrderBook;
import api.core.Side;
import api.messages.requests.IPlaceOrderRequest;
import api.messages.trading.MicroFIXSide;
import api.messages.trading.response.IMicroFIXResponse;
import api.messages.trading.response.IMicroFIXResponseFactory;
import authenticationdb.AuthenticationDBConnection;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import impl.messages.trading.response.MicroFIXResponseFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import server.ExchangeServerContext;
import server.web.WebServletsShared;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/placeOrder")
public class PlaceOrderWebServlet extends HttpServlet {

    private final IMicroFIXResponseFactory externalResponseFactory = new MicroFIXResponseFactory();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestString = WebServletsShared.getRequestString(request);
        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);;
        WebPlaceOrderRequest webPlaceOrderRequest = objectMapper.readValue(requestString, WebPlaceOrderRequest.class);
        try {
            if (AuthenticationDBConnection.getInstance().getUserType(webPlaceOrderRequest.userID, webPlaceOrderRequest.password()) == -1) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        IMicroFIXResponse microFIXResponse = sendPlaceOrderRequest(webPlaceOrderRequest);
        objectMapper.writeValue(response.getWriter(), microFIXResponse);
    }

    private IMicroFIXResponse sendPlaceOrderRequest(WebPlaceOrderRequest webPlaceOrderRequest) {
        IMatchingEngine matchingEngine = ExchangeServerContext.getInstance().getMatchingEngine();
        MicroFIXSide microFIXSide = null;
        try {
            microFIXSide = MicroFIXSide.fromString(webPlaceOrderRequest.side);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!matchingEngine.containsOrderBook(webPlaceOrderRequest.bookID)) {
            return externalResponseFactory.getErrorAckResponse(
                    webPlaceOrderRequest.bookID,
                    webPlaceOrderRequest.userID,
                    webPlaceOrderRequest.price,
                    microFIXSide,
                    webPlaceOrderRequest.volume,
                    webPlaceOrderRequest.externalTimestamp
            );
        }
        IOrderBook orderBook = matchingEngine.getOrderBook(webPlaceOrderRequest.bookID);
        Side side = microFIXSide == MicroFIXSide.BUY ? Side.BUY : Side.SELL;
        IPlaceOrderRequest placeOrderRequest = orderBook.getOrderRequestFactory().createPlaceOrderRequest(
                webPlaceOrderRequest.userID,
                webPlaceOrderRequest.price,
                side,
                webPlaceOrderRequest.volume
        );
        int externalTimestamp = webPlaceOrderRequest.externalTimestamp;
        orderBook.placeOrder(placeOrderRequest);
        return externalResponseFactory.getReceivedPlaceOrderAckResponse(
                placeOrderRequest.getBookID(),
                placeOrderRequest.getUserID(),
                placeOrderRequest.getOrderID(),
                placeOrderRequest.getPrice(),
                placeOrderRequest.getSide() == Side.BUY ? MicroFIXSide.BUY : MicroFIXSide.SELL,
                placeOrderRequest.getTotalVolume(),
                externalTimestamp
        );
    }

    private record WebPlaceOrderRequest(int userID, String password, String bookID, double price, String side, int volume, int externalTimestamp) { }

}
