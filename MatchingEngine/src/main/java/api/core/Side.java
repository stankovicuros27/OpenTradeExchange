package api.core;

import java.io.IOException;

public enum Side {

    BUY {
        @Override
        public Side getCounterSide() {
            return SELL;
        }

        @Override
        public java.lang.String toString() {
            return "BUY";
        }
    },
    SELL {
        @Override
        public Side getCounterSide() {
            return BUY;
        }

        @Override
        public java.lang.String toString() {
            return "SELL";
        }
    };

    public abstract Side getCounterSide();

    public static Side fromString(String sideString) throws IOException {
        if (sideString.equals("BUY")) {
            return Side.BUY;
        } else if (sideString.equals("SELL")) {
            return Side.SELL;
        } else {
            throw new IOException();
        }
    }
}
