package gui;

/** Thrown when the user's input is invalid. The message is shown to the user as is. */
public class InputException extends Exception {
    private static final long serialVersionUID = 1L;

    public InputException(String message) {
        super(message);
    }
}
