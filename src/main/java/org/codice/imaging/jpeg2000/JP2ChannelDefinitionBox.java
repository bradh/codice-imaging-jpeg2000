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
 * A JPEG2000 Channel Definition Box, as defined in JPEG core specification
 * Annex I Section 5.3.6
 */
public class JP2ChannelDefinitionBox {

    private JP2Reader mReader = null;
    private int mBoxLength = 0;
    private JP2ChannelDefinitionEntry[] mEntries = null;

    /**
     * Construct ChannelDefinitionBox from specified reader.
     *
     * This will read a specified number of bytes (the boxLength) from the
     * reader, or will throw a parsing exception.
     *
     * @param reader the reader to read from
     * @param boxLength the number of bytes in the box
     *
     * @throws JP2ParsingException
     */
    public JP2ChannelDefinitionBox(final JP2Reader reader, final int boxLength) throws JP2ParsingException {
        mReader = reader;
        mBoxLength = boxLength;
        parseBox();
    }

    private void parseBox() throws JP2ParsingException {
        int numberOfChannelDescriptors = mReader.readUnsignedShort();
        int remainingBytes = mBoxLength - 2; // TODO: use a contant
        int expectedRemainingBytes = numberOfChannelDescriptors * JP2ChannelDefinitionEntry.numberOfBytesInOneEntry();
        if (remainingBytes != expectedRemainingBytes) {
            throw new JP2ParsingException("Unexpected box length for JP2ChannelDefinitionBox:" + remainingBytes + ", expected:" + expectedRemainingBytes);            
        }
        mEntries = new JP2ChannelDefinitionEntry[numberOfChannelDescriptors];
        for (int i = 0; i < mEntries.length; ++i) {
            mEntries[i] = new JP2ChannelDefinitionEntry(mReader);
        }
    }

}
