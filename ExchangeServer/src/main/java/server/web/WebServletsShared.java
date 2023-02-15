package server.web;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.web.trade.PlaceOrderWebServlet;

import java.io.BufferedReader;

public enum WebServletsShared {
    ;

    private static final Logger LOGGER = LoggerFactory.getLogger(WebServletsShared.class);

    public static String getRequestString(HttpServletRequest request) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader reader = request.getReader();
            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (Exception e) {
            LOGGER.info("Bad request");
        }
        return stringBuilder.toString();
    }

}
