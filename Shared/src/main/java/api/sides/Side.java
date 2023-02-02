package api.sides;

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
}
