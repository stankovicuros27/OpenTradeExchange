package impl.messages.authentication;

import api.messages.authentication.IMicroFIXAuthenticationResponse;

public class MicroFIXAuthenticationResponse implements IMicroFIXAuthenticationResponse {

    private final int userID;
    private final boolean accepted;
    private final int timestamp;

    MicroFIXAuthenticationResponse(int userID, boolean accepted, int timestamp) {
        this.userID = userID;
        this.accepted = accepted;
        this.timestamp = timestamp;
    }

    @Override
    public int getUserID() {
        return userID;
    }

    @Override
    public boolean isAccepted() {
        return accepted;
    }

    @Override
    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "MicroFIXAuthenticationResponse{" +
                "userID=" + userID +
                ", accepted=" + accepted +
                ", timestamp=" + timestamp +
                '}';
    }
}
