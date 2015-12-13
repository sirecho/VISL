/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.visl.exceptions;

/**
 *
 * @author echo
 */
public class InvalidDimensionsException extends RuntimeException {

    public InvalidDimensionsException(String string) {
        super(string);
    }
}
