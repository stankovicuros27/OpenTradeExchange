package impl.messages.responses;

import api.messages.responses.IErrorAckResponse;
import api.messages.responses.ResponseType;

public class ErrorAckResponse implements IErrorAckResponse {

    private final String bookID;
    private final int userID;
    private final int timestamp;

    public ErrorAckResponse(String bookID, int userID, int timestamp) {
        this.bookID = bookID;
        this.userID = userID;
        this.timestamp = timestamp;
    }

    @Override
    public String getBookID() {
        return bookID;
    }

    @Override
    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public int getUserID() {
        return userID;
    }

    @Override
    public ResponseType getType() {
        return ResponseType.ErrorAckResponse;
    }
}
