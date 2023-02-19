package server.web.users;

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

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/setUserType")
public class SetUserTypeWebServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestString = WebServletsShared.getRequestString(request);
        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        WebSetUserTypeRequest webSetUserTypeRequest = objectMapper.readValue(requestString, WebSetUserTypeRequest.class);
        try {
            if (AuthenticationDBConnection.getInstance().getUserType(webSetUserTypeRequest.userID, webSetUserTypeRequest.password()) != UserTypeConstants.USER_TYPE_ADMIN) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            if (!AuthenticationDBConnection.getInstance().isUserIDExists(webSetUserTypeRequest.userIDToSet)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "User doesn't exist!");
                return;
            }
            AuthenticationDBConnection.getInstance().setUserType(webSetUserTypeRequest.userIDToSet, webSetUserTypeRequest.userType);
            objectMapper.writeValue(response.getWriter(), "User type changed!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private record WebSetUserTypeRequest(int userID, String password, int userIDToSet, int userType) { }

}
