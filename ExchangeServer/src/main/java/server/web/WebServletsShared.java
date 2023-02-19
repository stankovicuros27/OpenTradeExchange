package server.web;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;

public enum WebServletsShared {
    ;

    private static final Logger LOGGER = LoggerFactory.getLogger(WebServletsShared.class);

    public static String getRequestString(HttpServletRequest request) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (Exception e) {
            LOGGER.info("Bad request");
            throw new RuntimeException(e);
        }
        return stringBuilder.toString();
    }

}
