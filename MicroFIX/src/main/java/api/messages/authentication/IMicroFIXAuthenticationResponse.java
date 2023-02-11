package api.messages.authentication;

public interface IMicroFIXAuthenticationResponse extends IMicroFIXAuthenticationMessage {
    public boolean isAccepted();
}
