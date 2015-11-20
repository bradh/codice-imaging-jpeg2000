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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bradh
 */
public class JP2ParseStrategy {

    private JP2Reader mReader = null;

    private String mBrand = null;
    private int mMinorVersion = 0;
    private final ArrayList<String> mCompatibilityList = new ArrayList<>();
    private final ArrayList<String> mXmlList = new ArrayList<>();
    private int mImageHeight = 0;
    private int mImageWidth = 0;
    private int mNumberOfComponents = 0;
    private int mColourspaceUnknown  = 0;
    private int mIntellectualPropertyRights = 0;
    private int mColourSpace = 0;

    private int mBitsPerComponent = 0;
    
    private JP2CodeStream mCodeStream = null;
    private JP2ChannelDefinitionBox mChannelDefinitionBox = null;

    private static final int BOX_SIGNATURE_FILETYPE = 0x66747970;
    private static final int BOX_SIGNATURE_LENGTH = 4;
    private static final int BRAND_STRING_LENGTH = 4;
    private static final int UNSIGNED_BYTE_LENGTH = 1;
    private static final int UNSIGNED_INT_LENGTH = 4;
    private static final int COMPATIBILITY_LIST_ENTRY_LENGTH = 4;
    private static final int EXPECTED_LENGTH_IMAGE_HEADER = 14; // Excludes LBox and TBox
    private static final int EXPECTED_COMPRESSION_TYPE_VALUE = 7;

    public void parse(final JP2Reader reader) throws JP2ParsingException {
        mReader = reader;
        parseFileTypeBox();
        while (mReader.hasDataRemaining()) {
            int boxLength = mReader.readUnsignedInt();
            // TODO: there is a "0" and "1" case we aren't handling. See Table I-1 and Section I.4
            String boxType = mReader.getFixedLengthString(BOX_SIGNATURE_LENGTH);
            int remainingBytesInBox = boxLength - (UNSIGNED_INT_LENGTH + BOX_SIGNATURE_LENGTH);
            switch (boxType) {
                case "xml ":
                    parseXMLBox(remainingBytesInBox);
                    break;
                case "jp2h":
                    parseJP2HeaderSuperBox(remainingBytesInBox);
                    break;
                case "jp2c":
                    parseContiguousCodestreamBox(remainingBytesInBox);
                    break;
                default:
                    mReader.skipBytes(remainingBytesInBox);
                    break;
            }
            
        }
    }

    private void parseContiguousCodestreamBox(int codestreamLength) throws JP2ParsingException {
        mCodeStream = new JP2CodeStream(mReader, codestreamLength);
    }

    private void parseJP2HeaderSuperBox(int superBoxLength) throws JP2ParsingException {
        int bytesRead = 0;
        // TODO: consider making ihdr appear here
        while (bytesRead < superBoxLength) {
            int boxLength = mReader.readUnsignedInt();
            bytesRead += UNSIGNED_INT_LENGTH;
            String boxType = mReader.getFixedLengthString(BOX_SIGNATURE_LENGTH);
            bytesRead += BOX_SIGNATURE_LENGTH;
            int remainingBytesInBox = boxLength - (UNSIGNED_INT_LENGTH + BOX_SIGNATURE_LENGTH);
            switch (boxType) {
                case "ihdr":
                    parseImageHeaderBox(remainingBytesInBox);
                    break;
                case "colr":
                    parseColourSpecificationBox(remainingBytesInBox);
                    break;
                case "cdef":
                    parseChannelDefinitionBox(remainingBytesInBox);
                    break;
                default:
                    mReader.skipBytes(remainingBytesInBox);
                    break;
            }
            bytesRead += remainingBytesInBox;
        }
    }

    private void parseImageHeaderBox(final int imageHeaderLength) throws JP2ParsingException {
        if (imageHeaderLength != EXPECTED_LENGTH_IMAGE_HEADER) {
            throw new JP2ParsingException("Bad ihdr length:" + imageHeaderLength);
        }
        mImageHeight = mReader.readUnsignedInt();
        mImageWidth = mReader.readUnsignedInt();
        mNumberOfComponents = mReader.readUnsignedShort();
        mBitsPerComponent = mReader.readUnsignedByte();
        int compressionType = mReader.readUnsignedByte();
        if (compressionType != EXPECTED_COMPRESSION_TYPE_VALUE) {
            throw new JP2ParsingException("Unexpected compression type:" + compressionType);
        }
        mColourspaceUnknown = mReader.readUnsignedByte();
        mIntellectualPropertyRights = mReader.readUnsignedByte();
    }

    private void parseColourSpecificationBox(final int remainingBytesInBox) throws JP2ParsingException {
        int method = mReader.readUnsignedByte();
        mReader.skipBytes(1); // PREC, always ignored
        mReader.skipBytes(1); // APPROX, always ignored
        if (method == 1) {
            mColourSpace = mReader.readUnsignedInt();
        } else if (method == 2) {
            parseRestrictedICCProfileColourSpecificationBox(remainingBytesInBox - (3 * UNSIGNED_BYTE_LENGTH));
        } else {
            mReader.skipBytes(remainingBytesInBox - (3 * UNSIGNED_BYTE_LENGTH));
        }
    }

    private void parseChannelDefinitionBox(final int remainingBytesInBox) throws JP2ParsingException {
        mChannelDefinitionBox = new JP2ChannelDefinitionBox(mReader, remainingBytesInBox);
    }

    private void parseRestrictedICCProfileColourSpecificationBox(final int bytesRemainingInBox) throws JP2ParsingException {
        mReader.skipBytes(bytesRemainingInBox);
    }

    private void parseXMLBox(int xmlLength) throws JP2ParsingException {
        String xml = mReader.getFixedLengthString(xmlLength);
        mXmlList.add(xml);
    }

    private void parseFileTypeBox() throws JP2ParsingException {
        int boxLength = mReader.readUnsignedInt();
        int boxType = mReader.readUnsignedInt();
        if (BOX_SIGNATURE_FILETYPE != boxType) {
            throw new JP2ParsingException("BoxType signature verification failure");
        }
        mBrand = mReader.getFixedLengthString(BRAND_STRING_LENGTH);
        mMinorVersion = mReader.readUnsignedInt();
        int bytesRemainingInBox = boxLength - (BOX_SIGNATURE_LENGTH + BRAND_STRING_LENGTH + 2 * UNSIGNED_INT_LENGTH);
        if (bytesRemainingInBox < (UNSIGNED_INT_LENGTH + COMPATIBILITY_LIST_ENTRY_LENGTH)) {
            throw new JP2ParsingException("File Type box did not have required compatibility list entries");
        }
        int compatibilityListEntriesCount = mReader.readUnsignedInt();
        bytesRemainingInBox -= UNSIGNED_INT_LENGTH;
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

    public List<String> getXmlList() {
        return mXmlList;
    }

    public int getImageWidth() {
        return mImageWidth;
    }

    public int getImageHeight() {
        return mImageHeight;
    }

    public int getNumberOfComponents() {
        return mNumberOfComponents;
    }

    public int getBitsPerComponent() {
        return mBitsPerComponent;
    }

    public boolean hasUnknownColourspace() {
        return (mColourspaceUnknown == 1);
    }

    public boolean hasIntellectualPropertyRights() {
        return (mIntellectualPropertyRights == 1);
    }

    public int getColourSpace() {
        return mColourSpace;
    }

    public JP2CodeStream getCodeStream() {
        return mCodeStream;
    }
}
