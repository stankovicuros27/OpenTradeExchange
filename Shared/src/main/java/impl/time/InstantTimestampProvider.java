package impl.time;

import api.time.ITimestampProvider;

import java.time.Instant;

public class InstantTimestampProvider implements ITimestampProvider {

    @Override
    public int getTimestampNow() {
        return (int) Instant.now().getEpochSecond();
    }

}
