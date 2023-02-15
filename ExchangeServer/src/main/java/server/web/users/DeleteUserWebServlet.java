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

@WebServlet("/deleteUser")
public class DeleteUserWebServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestString = WebServletsShared.getRequestString(request);
        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        WebDeleteUserRequest webDeleteUserRequest = objectMapper.readValue(requestString, WebDeleteUserRequest.class);
        try {
            if (AuthenticationDBConnection.getInstance().getUserType(webDeleteUserRequest.userID, webDeleteUserRequest.password()) != UserTypeConstants.USER_TYPE_ADMIN) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            AuthenticationDBConnection.getInstance().deleteUser(webDeleteUserRequest.userIDToDelete);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        objectMapper.writeValue(response.getWriter(), "User deleted!");
    }

    private record WebDeleteUserRequest(int userID, String password, int userIDToDelete) { }

}
