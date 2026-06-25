package hexlet.code.app.exception;

public class LabelInUseException extends RuntimeException {
    public LabelInUseException(String message) {
        super(message);
    }
}
