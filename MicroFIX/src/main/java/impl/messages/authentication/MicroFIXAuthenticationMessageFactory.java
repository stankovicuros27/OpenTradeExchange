package impl.messages.authentication;

import api.messages.authentication.IMicroFIXAuthenticationMessageFactory;
import api.messages.authentication.IMicroFIXAuthenticationRequest;
import api.messages.authentication.IMicroFIXAuthenticationResponse;
import api.time.ITimestampProvider;
import impl.time.InstantTimestampProvider;

public class MicroFIXAuthenticationMessageFactory implements IMicroFIXAuthenticationMessageFactory {

    private final ITimestampProvider timestampProvider = new InstantTimestampProvider();

    @Override
    public IMicroFIXAuthenticationRequest getAuthenticationRequest(int userID, String passwordHash) {
        int timestamp = timestampProvider.getTimestampNow();
        return new MicroFIXAuthenticationRequest(userID, passwordHash, timestamp);
    }

    @Override
    public IMicroFIXAuthenticationResponse getAuthenticationResponse(int userID, boolean accepted) {
        int timestamp = timestampProvider.getTimestampNow();
        return new MicroFIXAuthenticationResponse(userID, accepted, timestamp);
    }
}
