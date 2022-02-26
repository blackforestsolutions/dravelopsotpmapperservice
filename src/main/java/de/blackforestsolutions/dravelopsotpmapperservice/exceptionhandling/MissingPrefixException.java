package de.blackforestsolutions.dravelopsotpmapperservice.exceptionhandling;

public class MissingPrefixException extends RuntimeException {

    public MissingPrefixException(String message) {
        super(message);
    }
}
