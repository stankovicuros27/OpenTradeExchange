package server.web.trade;

import api.core.IMatchingEngine;
import api.core.IOrderBook;
import api.messages.requests.ICancelOrderRequest;
import api.messages.responses.IOrderStatusResponse;
import api.messages.responses.OrderResponseStatus;
import api.messages.trading.response.IMicroFIXResponse;
import api.messages.trading.response.IMicroFIXResponseFactory;
import authenticationdb.AuthenticationDBConnection;
import authenticationdb.UserTypeConstants;
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
import tradingdatadb.TradingDataDBConnection;

import java.io.IOException;
import java.sql.SQLException;


@WebServlet("/cancelOrder")
public class CancelOrderWebServlet extends HttpServlet {

    private final IMicroFIXResponseFactory externalResponseFactory = new MicroFIXResponseFactory();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestString = WebServletsShared.getRequestString(request);
        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        WebCancelOrderRequest webCancelOrderRequest = objectMapper.readValue(requestString, WebCancelOrderRequest.class);
        try {
            if (AuthenticationDBConnection.getInstance().getUserType(webCancelOrderRequest.userID, webCancelOrderRequest.password()) <= UserTypeConstants.USER_TYPE_NOT_ACCEPTED) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            IMatchingEngine matchingEngine = ExchangeServerContext.getInstance().getMatchingEngine();
            if (!matchingEngine.containsOrderBook(webCancelOrderRequest.bookID)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "BookID doesn't exist!");
                return;
            }
            IMicroFIXResponse microFIXResponse = sendCancelOrderRequest(webCancelOrderRequest);
            objectMapper.writeValue(response.getWriter(), microFIXResponse);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private IMicroFIXResponse sendCancelOrderRequest(WebCancelOrderRequest webCancelOrderRequest) {
        IMatchingEngine matchingEngine = ExchangeServerContext.getInstance().getMatchingEngine();
        IOrderBook orderBook = matchingEngine.getOrderBook(webCancelOrderRequest.bookID);
        ICancelOrderRequest cancelOrderRequest = orderBook.getOrderRequestFactory().createCancelOrderRequest(
                webCancelOrderRequest.userID,
                webCancelOrderRequest.orderID
        );
        int externalTimestamp = webCancelOrderRequest.externalTimestamp;
        IOrderStatusResponse orderStatusResponse = orderBook.cancelOrder(cancelOrderRequest);
        if (orderStatusResponse.getStatus() == OrderResponseStatus.NULL_ORDER || orderStatusResponse.getStatus() == OrderResponseStatus.NULL_ORDER) {
            return externalResponseFactory.getErrorResponse(
                    webCancelOrderRequest.bookID,
                    webCancelOrderRequest.userID,
                    webCancelOrderRequest.orderID
            );
        }
        IMicroFIXResponse cancelOrderAckResponse = externalResponseFactory.getReceivedCancelOrderAckResponse(
                cancelOrderRequest.getBookID(),
                cancelOrderRequest.getUserID(),
                cancelOrderRequest.getOrderID(),
                externalTimestamp
        );
        TradingDataDBConnection.getInstance().insertCancelOrder(cancelOrderAckResponse);
        return cancelOrderAckResponse;
    }

    private record WebCancelOrderRequest(int userID, String password, String bookID, int orderID, int externalTimestamp) { }

}
