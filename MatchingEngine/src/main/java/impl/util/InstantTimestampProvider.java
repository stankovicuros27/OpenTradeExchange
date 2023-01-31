package impl.util;

import api.util.ITimestampProvider;

import java.time.Instant;

public class InstantTimestampProvider implements ITimestampProvider {

    @Override
    public int getTimestampNow() {
        return (int) Instant.now().getEpochSecond();
    }

}
