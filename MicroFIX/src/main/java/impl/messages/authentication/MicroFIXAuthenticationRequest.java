package impl.messages.authentication;

import api.messages.authentication.IMicroFIXAuthenticationRequest;

public class MicroFIXAuthenticationRequest implements IMicroFIXAuthenticationRequest {

    private final int userID;
    private final String password;
    private final int timestamp;

    MicroFIXAuthenticationRequest(int userID, String password, int timestamp) {
        this.userID = userID;
        this.password = password;
        this.timestamp = timestamp;
    }

    @Override
    public int getUserID() {
        return userID;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public int getTimestamp() {
        return timestamp;
    }

}
