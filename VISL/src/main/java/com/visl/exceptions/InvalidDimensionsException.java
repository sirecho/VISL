package com.visl.exceptions;

/**
 * Exception for use when OpenCV encounters invalid image dimensions.
 */
public class InvalidDimensionsException extends RuntimeException {

    public InvalidDimensionsException(String string) {
        super(string);
    }
}
