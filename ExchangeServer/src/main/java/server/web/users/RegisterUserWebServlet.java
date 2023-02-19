package server.web.users;

import authenticationdb.AuthenticationDBConnection;
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

@WebServlet("/registerUser")
public class RegisterUserWebServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestString = WebServletsShared.getRequestString(request);
        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        WebRegisterUserRequest webRegisterUserRequest = objectMapper.readValue(requestString, WebRegisterUserRequest.class);
        try {
            if (AuthenticationDBConnection.getInstance().isUsernameExists(webRegisterUserRequest.username)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Username already exists!");
                return;
            }
            AuthenticationDBConnection.getInstance().registerUser(webRegisterUserRequest.username, webRegisterUserRequest.password);
            objectMapper.writeValue(response.getWriter(), "User submitted!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private record WebRegisterUserRequest(String username, String password) { }

}
