package server.web.users;

import authenticationdb.AuthenticationDBConnection;
import authenticationdb.User;
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
import java.util.List;

@WebServlet("/getAllUsers")
public class GetAllUsersWebServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestString = WebServletsShared.getRequestString(request);
        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        WebGetAllUsersRequest webSetUserTypeRequest = objectMapper.readValue(requestString, WebGetAllUsersRequest.class);
        try {
            if (AuthenticationDBConnection.getInstance().getUserType(webSetUserTypeRequest.userID, webSetUserTypeRequest.password()) != UserTypeConstants.USER_TYPE_ADMIN) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            List<User> allUsers = AuthenticationDBConnection.getInstance().getAllUsers();
            objectMapper.writeValue(response.getWriter(), allUsers);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private record WebGetAllUsersRequest(int userID, String password) { }

}
