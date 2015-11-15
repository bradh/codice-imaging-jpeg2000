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
public class JP2Parser {

    /**
     * Parse a JP2 file from a specific reader and parsing strategy.
     *
     * The concept is that the parsing strategy will store the parse results.
     *
     * @param reader the reader to use
     * @param parseStrategy the parsing strategy
     * @throws JP2ParsingException if an error occurs during parsing
     */
    public static void parse(final JP2Reader reader, final JP2ParseStrategy parseStrategy) throws JP2ParsingException {
        readJPEG2000SignatureBox(reader);
        parseStrategy.parse(reader);
    }

    private static void readJPEG2000SignatureBox(final JP2Reader reader) {
        
    }
}
