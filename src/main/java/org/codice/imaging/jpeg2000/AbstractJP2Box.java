/*
 * The MIT License
 *
 * Copyright 2015 Codice.
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
public abstract class AbstractJP2Box {
    
    protected JP2Reader mReader = null;
    protected int mBoxLength = 0;
    
    /**
     * Construct box from specified reader.
     *
     * This will read a specified number of bytes (the boxLength) from the
     * reader, or will throw a parsing exception.
     *
     * @param reader the reader to read from
     * @param boxLength the number of bytes in the box
     *
     * @throws JP2ParsingException
     */
    public AbstractJP2Box(final JP2Reader reader, final int boxLength) throws JP2ParsingException {
        mReader = reader;
        mBoxLength = boxLength;
    }
}
