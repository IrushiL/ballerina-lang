package org.ballerinalang.cli.bbgen.exceptions;

/**
 * Class for creating BBGen tool's exceptions.
 */
public class BBGenException extends Exception {

    /**
     * Constructs an BBGenException with the specified detail message.
     *
     * @param message the detail message
     */
    public BBGenException(String message) {

        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public BBGenException(String message, Throwable cause) {

        super(message, cause);
    }
}
