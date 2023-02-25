package api.messages.trading;

import java.io.IOException;

public enum MicroFIXSide {
    BUY {
        @Override
        public String toString() {
            return "BUY";
        }
    },
    SELL {
        @Override
        public String toString() {
            return "SELL";
        }
    };

    public static MicroFIXSide fromString(String sideString) throws IOException {
        if (sideString.equals("BUY")) {
            return BUY;
        } else if (sideString.equals("SELL")) {
            return SELL;
        }
        throw new IOException();
    }
}
