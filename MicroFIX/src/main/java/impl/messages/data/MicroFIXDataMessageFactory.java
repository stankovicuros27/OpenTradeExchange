package impl.messages.data;

import api.messages.data.IMicroFIXDataMessageFactory;
import api.messages.data.IMicroFIXL1DataMessage;
import api.time.ITimestampProvider;
import impl.time.InstantTimestampProvider;

public class MicroFIXDataMessageFactory implements IMicroFIXDataMessageFactory {

    private final ITimestampProvider timestampProvider = new InstantTimestampProvider();

    @Override
    public IMicroFIXL1DataMessage getL1DataMessage(String bookID, double bestBuyPrice, int totalBuyVolume, double bestSellPrice, int totalSellVolume, double lastTradePrice) {
        int timestamp = timestampProvider.getTimestampNow();
        return new MicroFIXL1DataMessage(bookID, timestamp, bestBuyPrice, totalBuyVolume, bestSellPrice, totalSellVolume, lastTradePrice);
    }

}
