package pl.sknikod.kodemy.util;

public class ExceptionRestGenericMessage {

    private long timeStamp;
    private int status;
    private String message;
    private String description;

    public ExceptionRestGenericMessage() {

    }

    public ExceptionRestGenericMessage(long timeStamp, int status, String message, String description) {
        this.timeStamp = timeStamp;
        this.status = status;
        this.message = message;
        this.description = description;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
