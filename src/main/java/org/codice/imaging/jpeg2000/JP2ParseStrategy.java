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
    private static final int EXPECTED_LENGTH_IMAGE_HEADER = 14; // Excludes LBox and TBox
    private static final int EXPECTED_COMPRESSION_TYPE_VALUE = 7;

    private JP2Reader mReader = null;

    private JP2FileTypeBox mFileTypeBox = null;
    private final ArrayList<JP2XmlBox> mXmlList = new ArrayList<>();
    private int mImageHeight = 0;
    private int mImageWidth = 0;
    private int mNumberOfComponents = 0;
    private int mColourspaceUnknown  = 0;
    private int mIntellectualPropertyRights = 0;
    private JP2ColourSpecificationBox mColourSpecificationBox = null;

    private int mBitsPerComponent = 0;
    
    private JP2CodeStream mCodeStream = null;
    private JP2ChannelDefinitionBox mChannelDefinitionBox = null;


    public void parse(final JP2Reader reader) throws JP2ParsingException {
        mReader = reader;
        int fileBoxLength = mReader.readUnsignedInt();
        mFileTypeBox = new JP2FileTypeBox(mReader, fileBoxLength);
        while (mReader.hasDataRemaining()) {
            int boxLength = mReader.readUnsignedInt();
            // TODO: there is a "0" and "1" case we aren't handling. See Table I-1 and Section I.4
            String boxType = mReader.getFixedLengthString(PackageConstants.BOX_SIGNATURE_LENGTH);
            int remainingBytesInBox = boxLength - (PackageConstants.UNSIGNED_INT_LENGTH + PackageConstants.BOX_SIGNATURE_LENGTH);
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
            bytesRead += PackageConstants.UNSIGNED_INT_LENGTH;
            String boxType = mReader.getFixedLengthString(PackageConstants.BOX_SIGNATURE_LENGTH);
            bytesRead += PackageConstants.BOX_SIGNATURE_LENGTH;
            int remainingBytesInBox = boxLength - (PackageConstants.UNSIGNED_INT_LENGTH + PackageConstants.BOX_SIGNATURE_LENGTH);
            switch (boxType) {
                case "ihdr":
                    parseImageHeaderBox(remainingBytesInBox);
                    break;
                case "colr":
                    mColourSpecificationBox = new JP2ColourSpecificationBox(mReader, remainingBytesInBox);
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

    private void parseChannelDefinitionBox(final int remainingBytesInBox) throws JP2ParsingException {
        if (mChannelDefinitionBox != null) {
            throw new JP2ParsingException("Duplicate Channel Definition Box entries.");
        }
        mChannelDefinitionBox = new JP2ChannelDefinitionBox(mReader, remainingBytesInBox);
    }

    private void parseXMLBox(int xmlLength) throws JP2ParsingException {
        mXmlList.add(new JP2XmlBox(mReader, xmlLength));
    }

    public JP2FileTypeBox getFileTypeBox() {
        return mFileTypeBox;
    }

    /**
     * Get the XML Boxes for this file.
     *
     * @return a list of XML boxes, possibly empty.
     */
    public List<JP2XmlBox> getXmlList() {
        return mXmlList;
    }

    /**
     * Get the channel definition box for the image.
     *
     * Channel definition box is not a required element.
     *
     * @return the box, or null if there is no channel definition box.
     */
    public JP2ChannelDefinitionBox getChannelDefinitionBox() {
        return mChannelDefinitionBox;
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

    public JP2ColourSpecificationBox getColourSpecification() {
        return mColourSpecificationBox;
    }

    public JP2CodeStream getCodeStream() {
        return mCodeStream;
    }
}
