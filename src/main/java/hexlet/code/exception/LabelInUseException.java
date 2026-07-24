package hexlet.code.exception;

public class LabelInUseException extends RuntimeException {
    public LabelInUseException(String message) {
        super(message);
    }
}
