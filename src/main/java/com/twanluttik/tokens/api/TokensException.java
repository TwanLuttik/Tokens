package com.twanluttik.tokens.api;

/**
 * Runtime exception thrown by the Tokens API when an operation fails.
 * <p>
 * This is an unchecked exception so plugin developers are not forced to
 * handle SQL or other internal errors unless they want to.
 */
public class TokensException extends RuntimeException {

    public TokensException(String message) {
        super(message);
    }

    public TokensException(String message, Throwable cause) {
        super(message, cause);
    }
}
