package server.web.trade;

import authenticationdb.AuthenticationDBConnection;
import authenticationdb.UserTypeConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import server.web.WebServletsShared;
import tradingdatadb.OrderModel;
import tradingdatadb.TradingDataDBConnection;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/getAllOrders")
public class GetAllOrdersWebServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestString = WebServletsShared.getRequestString(request);
        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);;
        WebGetAllOrdersRequest webGetAllOrdersRequest = objectMapper.readValue(requestString, WebGetAllOrdersRequest.class);
        try {
            if (AuthenticationDBConnection.getInstance().getUserType(webGetAllOrdersRequest.userID, webGetAllOrdersRequest.password()) <= UserTypeConstants.USER_TYPE_NOT_ACCEPTED) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            List<OrderModel> orders = TradingDataDBConnection.getInstance().getAllOrders(webGetAllOrdersRequest.userID);
            objectMapper.writeValue(response.getWriter(), orders);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private record WebGetAllOrdersRequest(int userID, String password) { }

}
