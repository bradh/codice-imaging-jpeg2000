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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bradh
 */
public class JP2FileTypeBox extends AbstractJP2Box {

    private static final int BOX_SIGNATURE_FILETYPE = 0x66747970;
    private static final int BRAND_STRING_LENGTH = 4;
    private static final int COMPATIBILITY_LIST_ENTRY_LENGTH = 4;
        
    private String mBrand = null;
    private int mMinorVersion = 0;
    
    private final ArrayList<String> mCompatibilityList = new ArrayList<>();
    
    /**
     * Construct FileType Box from specified reader.
     *
     * This will read a specified number of bytes (the boxLength) from the
     * reader, or will throw a parsing exception.
     *
     * @param reader the reader to read from
     * @param boxLength the number of bytes in the box
     *
     * @throws JP2ParsingException
     */
    public JP2FileTypeBox(JP2Reader reader, int boxLength) throws JP2ParsingException {
        super(reader, boxLength);
        parseBox();
    }
    
    private void parseBox() throws JP2ParsingException {
        int boxType = mReader.readUnsignedInt();
        if (BOX_SIGNATURE_FILETYPE != boxType) {
            throw new JP2ParsingException("BoxType signature verification failure");
        }
        mBrand = mReader.getFixedLengthString(BRAND_STRING_LENGTH);
        mMinorVersion = mReader.readUnsignedInt();
        int bytesRemainingInBox = mBoxLength - (PackageConstants.BOX_SIGNATURE_LENGTH + BRAND_STRING_LENGTH + 2 * PackageConstants.UNSIGNED_INT_LENGTH);
        if (bytesRemainingInBox < (PackageConstants.UNSIGNED_INT_LENGTH + COMPATIBILITY_LIST_ENTRY_LENGTH)) {
            throw new JP2ParsingException("File Type box did not have required compatibility list entries");
        }
        int compatibilityListEntriesCount = mReader.readUnsignedInt();
        bytesRemainingInBox -= PackageConstants.UNSIGNED_INT_LENGTH;
        if (compatibilityListEntriesCount * COMPATIBILITY_LIST_ENTRY_LENGTH != bytesRemainingInBox) {
            throw new JP2ParsingException("File Type box has mismatched compatibility list counts");
        }
        for (int i = 0; i < bytesRemainingInBox; i += COMPATIBILITY_LIST_ENTRY_LENGTH) {
            mCompatibilityList.add(mReader.getFixedLengthString(COMPATIBILITY_LIST_ENTRY_LENGTH));
        }
    }
    
    public String getBranding() {
        return mBrand;
    }

    public int getMinorVersion() {
        return mMinorVersion;
    }

    public List<String> getCompatibilityList() {
        return mCompatibilityList;
    }
}
