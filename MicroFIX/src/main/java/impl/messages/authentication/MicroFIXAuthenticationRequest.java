package impl.messages.authentication;

import api.messages.authentication.IMicroFIXAuthenticationRequest;

public class MicroFIXAuthenticationRequest implements IMicroFIXAuthenticationRequest {

    private final int userID;
    private final String passwordHash;
    private final int timestamp;

    MicroFIXAuthenticationRequest(int userID, String passwordHash, int timestamp) {
        this.userID = userID;
        this.passwordHash = passwordHash;
        this.timestamp = timestamp;
    }

    @Override
    public int getUserID() {
        return userID;
    }

    @Override
    public String getPasswordHash() {
        return passwordHash;
    }

    @Override
    public int getTimestamp() {
        return timestamp;
    }

}
