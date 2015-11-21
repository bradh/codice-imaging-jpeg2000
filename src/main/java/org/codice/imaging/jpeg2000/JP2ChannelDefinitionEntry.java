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
 * A JP2ChannelDefinitionEntry represents one entry in a Channel Definition Box.
 * 
 * Its basically one (of the N) Channel index / Channel type / Channel 
 * association, as described in I.5.3.6 of the core standard.
 */
public class JP2ChannelDefinitionEntry {

    private final int mChannelIndex;
    private final int mChannelType;
    private final int mChannelAssociation;

    static int numberOfBytesInOneEntry() {
        return 3 * PackageConstants.UNSIGNED_SHORT_LENGTH;
    }

    /**
     * Construct a single channel entry from the data provided by the reader
     * @param reader the reader to read from
     * @throws JP2ParsingException if parsing fails
     */
    public JP2ChannelDefinitionEntry(JP2Reader reader) throws JP2ParsingException {
        mChannelIndex = reader.readUnsignedShort();
        mChannelType = reader.readUnsignedShort();
        mChannelAssociation = reader.readUnsignedShort();
    }    
    
    public int getChannelIndex() {
        return mChannelIndex;
    }

    public int getChannelType() {
        return mChannelType;
    }

    public int getChannelAssociation() {
        return mChannelAssociation;
    }
}
