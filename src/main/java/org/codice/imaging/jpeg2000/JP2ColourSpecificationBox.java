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
 * A JPEG2000 XML Box, as defined in JPEG core specification
 * Annex I Section 5.3.3.
 */
public class JP2ColourSpecificationBox extends AbstractJP2Box {
    public static final int ENUM_COLOUR_SPACE_SRGB = 16;
    public static final int ENUM_COLOUR_SPACE_GREYSCALE = 17;
    
    private int mColourSpace = -1;


    /**
     * Construct Colour Specification Box from specified reader.
     *
     * This will read a specified number of bytes (the boxLength) from the
     * reader, or will throw a parsing exception.
     *
     * @param reader the reader to read from
     * @param boxLength the number of bytes in the box
     *
     * @throws JP2ParsingException
     */
    public JP2ColourSpecificationBox(JP2Reader reader, int boxLength) throws JP2ParsingException {
        super(reader, boxLength);
        parseBox();
    }

    private void parseBox() throws JP2ParsingException {
        int method = mReader.readUnsignedByte();
        mReader.skipBytes(PackageConstants.UNSIGNED_BYTE_LENGTH); // PREC, always ignored
        mReader.skipBytes(PackageConstants.UNSIGNED_BYTE_LENGTH); // APPROX, always ignored
        if (method == 1) {
            mColourSpace = mReader.readUnsignedInt();
        } else if (method == 2) {
            parseRestrictedICCProfileColourSpecificationBox(mBoxLength - (3 * PackageConstants.UNSIGNED_BYTE_LENGTH));
        } else {
            mReader.skipBytes(mBoxLength - (3 * PackageConstants.UNSIGNED_BYTE_LENGTH));
        }
    }
    
    private void parseRestrictedICCProfileColourSpecificationBox(final int bytesRemainingInBox) throws JP2ParsingException {
        mReader.skipBytes(bytesRemainingInBox);
    }

    /**
     * The colour space for the image.
     *
     * This is basically an enumerated value, and is specified in Table I-10 of
     * the core specification.
     *
     * @return -1 if not enumerated, 16 for sRGB (ENUM_COLOUR_SPACE_SRGB), 17 for greyscale (ENUM_COLOUR_SPACE_GREYSCALE)
     */
    public int getColourSpace() {
        return mColourSpace;
    }
    
    
}
