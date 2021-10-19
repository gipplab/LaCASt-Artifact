package gov.nist.drmf.interpreter.generic.backlog;

/**
 * Java class for handling exceptions
 */
public class InvalidLaTeXException extends Exception {

    /**
     * Exception thrown when invalid LaTeX is detected
     * @param message
     */
    public InvalidLaTeXException(String message) {
        super(message);
    }
}
