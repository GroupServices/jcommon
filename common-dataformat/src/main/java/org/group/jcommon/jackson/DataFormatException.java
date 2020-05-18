package org.group.jcommon.jackson;

/**
 * Defines DataFormatException to extends {@link RuntimeException}. Throw
 * current defines occurred expected exceptions when deal with data format.
 */
public class DataFormatException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public DataFormatException(String message) {
        super(message);
    }

    public DataFormatException(Throwable throwable) {
        super(throwable);
    }

    public DataFormatException(String message, Throwable throwable) {
        super(message, throwable);
    }
}