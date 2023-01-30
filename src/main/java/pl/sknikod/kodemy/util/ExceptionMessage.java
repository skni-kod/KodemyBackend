package pl.sknikod.kodemy.util;

public enum ExceptionMessage {
    PROVIDER_NOT_SUPPORT("The OAuth2 provider used is not supported yet.");

    public final String message;
    private ExceptionMessage(String message) {
        this.message = message;
    }
}