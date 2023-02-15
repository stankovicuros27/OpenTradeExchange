package api.messages.trading;

import java.io.IOException;

public enum MicroFIXSide {
    BUY,
    SELL;

    public static MicroFIXSide fromString(String sideString) throws IOException {
        if (sideString.equals("BUY")) {
            return BUY;
        } else if (sideString.equals("SELL")) {
            return SELL;
        }
        throw new IOException();
    }
}
