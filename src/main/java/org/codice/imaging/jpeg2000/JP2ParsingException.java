/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codice.imaging.jpeg2000;

/**
 *
 * @author bradh
 */
public class JP2ParsingException extends Exception {
    public JP2ParsingException () {

    }

    public JP2ParsingException (String message) {
        super (message);
    }

    public JP2ParsingException (Throwable cause) {
        super (cause);
    }

    public JP2ParsingException (String message, Throwable cause) {
        super (message, cause);
    }
}