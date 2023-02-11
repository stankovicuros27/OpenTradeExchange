package api.messages.authentication;

public interface IMicroFIXAuthenticationMessageFactory {
    public IMicroFIXAuthenticationRequest getAuthenticationRequest(int userID, String passwordHash);
    public IMicroFIXAuthenticationResponse getAuthenticationResponse(int userID, boolean accepted);
}
