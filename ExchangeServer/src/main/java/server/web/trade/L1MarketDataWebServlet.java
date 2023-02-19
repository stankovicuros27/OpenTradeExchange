package server.web.trade;

import api.core.IMatchingEngine;
import api.core.IOrderBook;
import api.core.Side;
import api.messages.data.IMicroFIXDataMessageFactory;
import api.messages.data.IMicroFIXL1DataMessage;
import api.messages.info.IOrderBookInfo;
import api.messages.trading.response.IMicroFIXResponseFactory;
import authenticationdb.AuthenticationDBConnection;
import authenticationdb.UserTypeConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import impl.messages.data.MicroFIXDataMessageFactory;
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

@WebServlet("/getL1Data")
public class L1MarketDataWebServlet extends HttpServlet {

    private final IMicroFIXResponseFactory externalResponseFactory = new MicroFIXResponseFactory();
    private final IMicroFIXDataMessageFactory microFIXDataMessageFactory = new MicroFIXDataMessageFactory();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestString = WebServletsShared.getRequestString(request);
        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        WebGetL1DataRequest webGetL1DataRequest = objectMapper.readValue(requestString, WebGetL1DataRequest.class);
        try {
            if (AuthenticationDBConnection.getInstance().getUserType(webGetL1DataRequest.userID, webGetL1DataRequest.password()) <= UserTypeConstants.USER_TYPE_PREMIUM) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        IMatchingEngine matchingEngine = ExchangeServerContext.getInstance().getMatchingEngine();
        if (!matchingEngine.containsOrderBook(webGetL1DataRequest.bookID)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "BookID doesn't exist!");
            return;
        }
        IOrderBook orderBook = matchingEngine.getOrderBook(webGetL1DataRequest.bookID);
        IMicroFIXL1DataMessage microFIXL1DataMessage = getL1DataMessage(orderBook);
        objectMapper.writeValue(response.getWriter(), microFIXL1DataMessage);
    }

    private IMicroFIXL1DataMessage getL1DataMessage(IOrderBook orderBook) {
        String bookID = orderBook.getBookID();
        IOrderBookInfo orderBookInfo = orderBook.getInfo();
        double bestBuyPrice = orderBookInfo.getBestPrice(Side.BUY);
        int totalBuyVolume = orderBookInfo.getLimitCollectionInfo(Side.BUY).getVolume();
        double bestSellPrice = orderBookInfo.getBestPrice(Side.SELL);
        int totalSellVolume = orderBookInfo.getLimitCollectionInfo(Side.SELL).getVolume();
        double lastTradePrice = orderBookInfo.getLastTradePrice();
        return microFIXDataMessageFactory.getL1DataMessage(bookID, bestBuyPrice, totalBuyVolume, bestSellPrice, totalSellVolume, lastTradePrice);
    }

    private record WebGetL1DataRequest(int userID, String password, String bookID) { }

}
