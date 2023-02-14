package api.messages.authentication;

public interface IMicroFIXAuthenticationRequest extends IMicroFIXAuthenticationMessage {
    public String getPassword();
}
