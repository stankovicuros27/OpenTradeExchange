package api.core;

public enum Side {

    BUY {
        @Override
        public Side getCounterSide() {
            return SELL;
        }
    },
    SELL {
        @Override
        public Side getCounterSide() {
            return BUY;
        }
    };

    public abstract Side getCounterSide();

}
