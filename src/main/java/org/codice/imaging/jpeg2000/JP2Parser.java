/*
 * The MIT License
 *
 * Copyright 2015 Codice
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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

    private static void readJPEG2000SignatureBox(final JP2Reader reader) throws JP2ParsingException {
        // TODO: make this verify validity
        reader.skipBytes(12);
    }
}
