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
public class JP2CodeStream {

    private static final int SIZ_MARKER_CODE = 0xFF51;
    private static final int SOC_MARKER_CODE = 0xFF4F;
    private static final int SOD_MARKER_CODE = 0xFF93;
    private static final int SOT_MARKER_CODE = 0xFF90;
    private static final int COD_MARKER_CODE = 0xFF52;
    private static final int QCD_MARKER_CODE = 0xFF5C;

    private JP2Reader mReader = null;
    private int mRemainingCodestreamLength = 0;
    private byte[] mCodestream = null;

    private int mVerticalOffsetOfReferenceTile;
    private int mHorizontalOffsetOfReferenceTile;
    private int mHeightOfReferenceTile;
    private int mWidthOfReferenceTile;
    private int mVerticalOffset;
    private int mHorizontalOffset;
    private int mYSize;
    private int mXSize;
    private int mRequiredCapabilities;

    public JP2CodeStream(JP2Reader reader, final int codestreamLength) throws JP2ParsingException {
        mReader = reader;
        mRemainingCodestreamLength = codestreamLength;
        parseMainHeader();
        mCodestream = mReader.getBytes(mRemainingCodestreamLength);
    }

    public byte[] getCodestreamBytes() {
        return mCodestream;
    }

    private void parseMainHeader() throws JP2ParsingException {
        int markerCode = mReader.readUnsignedShort();
        if (markerCode != SOC_MARKER_CODE) {
            throw new JP2ParsingException("Unexpected marker code, expected SOC_MARKER_CODE but got " + markerCode);
        }
        mRemainingCodestreamLength -= 2;
        parseImageAndTileSize();
        while (mRemainingCodestreamLength > 0) {
            markerCode = mReader.readUnsignedShort();
            mRemainingCodestreamLength -= 2;
            switch (markerCode) {
                case COD_MARKER_CODE: {
                    parseCodingStyleDefault();
                    break;
                }
                case SOT_MARKER_CODE: {
                    parseStartOfTilePart();
                    break;
                }
                case SOD_MARKER_CODE: {
                    // TODO: extract data
                    mReader.skipBytes(mRemainingCodestreamLength);
                    mRemainingCodestreamLength = 0;
                    break;
                }
                case QCD_MARKER_CODE: {
                    parseQuantizationDefault();
                    break;
                }
                default: {
                    int markerLength = mReader.readUnsignedShort();
                    mReader.skipBytes(markerLength - 2);
                    mRemainingCodestreamLength -=  (markerLength - 2);
                    break;
                }
            }
        }
    }

    private void parseImageAndTileSize() throws JP2ParsingException {
        int markerCode = mReader.readUnsignedShort();
        if (markerCode != SIZ_MARKER_CODE) {
            throw new JP2ParsingException("Unexpected marker code, expected SIZ_MARKER_CODE but got " + markerCode);
        }
        mRemainingCodestreamLength -= 2;
        int lengthOfMarkerSegmentInBytes = mReader.readUnsignedShort();
        mRemainingCodestreamLength -= lengthOfMarkerSegmentInBytes;
        mRequiredCapabilities = mReader.readUnsignedShort();
        mXSize = mReader.readUnsignedInt();
        mYSize = mReader.readUnsignedInt();
        mHorizontalOffset = mReader.readUnsignedInt();
        mVerticalOffset = mReader.readUnsignedInt();
        mWidthOfReferenceTile = mReader.readUnsignedInt();
        mHeightOfReferenceTile = mReader.readUnsignedInt();
        mHorizontalOffsetOfReferenceTile = mReader.readUnsignedInt();
        mVerticalOffsetOfReferenceTile = mReader.readUnsignedInt();
        int numberOfComponentsInImage = mReader.readUnsignedShort();
        // TODO: parse this properly
        for (int i = 0; i < numberOfComponentsInImage; ++i) {
            mReader.skipBytes(3);
        }
    }

    // TODO: make these values member variables
    private void parseStartOfTilePart() throws JP2ParsingException {
        int markerLength = mReader.readUnsignedShort();
        if (markerLength != (2 * PackageConstants.UNSIGNED_SHORT_LENGTH + PackageConstants.UNSIGNED_INT_LENGTH + 2 * PackageConstants.UNSIGNED_BYTE_LENGTH)) {
            throw new JP2ParsingException("Invalid length for SOT part:" + markerLength);
        }
        int isot = mReader.readUnsignedShort();
        int psot = mReader.readUnsignedInt();
        int tpsot = mReader.readUnsignedByte();
        int tnsot = mReader.readUnsignedByte();
        mRemainingCodestreamLength -= (markerLength - 2);
    }

    private void parseCodingStyleDefault() throws JP2ParsingException {
        int markerLength = mReader.readUnsignedShort();
        // TODO: these should be member variables
        int scod = mReader.readUnsignedByte();
        int progressionOrder = mReader.readUnsignedByte();
        int numberOfLayers = mReader.readUnsignedShort();
        int multipleComponentsTransformation = mReader.readUnsignedByte();
        int numberOfDecompositionLevels = mReader.readUnsignedByte();
        int codeBlockWidth = mReader.readUnsignedByte();
        int codeBlockHeight = mReader.readUnsignedByte();
        int codeBlockStyle = mReader.readUnsignedByte();
        int transformation = mReader.readUnsignedByte();
        // TODO: add precinct size parsing here.
        mReader.skipBytes(markerLength - 12);
        mRemainingCodestreamLength -= (markerLength - 2);
    }

    private void parseQuantizationDefault() throws JP2ParsingException {
        int markerLength = mReader.readUnsignedShort();
        int quantizationStyleForAllComponents = mReader.readUnsignedByte();
        // TODO: read quantization step values here = see A6.4
        mReader.skipBytes(markerLength - 3);
        mRemainingCodestreamLength -= (markerLength - 2);
    }

    public int getRequiredCapabilities() {
        return mRequiredCapabilities;
    }

    public int getVerticalOffsetOfReferenceTile() {
        return mVerticalOffsetOfReferenceTile;
    }

    public int getHorizontalOffsetOfReferenceTile() {
        return mHorizontalOffsetOfReferenceTile;
    }

    public int getHeightOfReferenceTile() {
        return mHeightOfReferenceTile;
    }

    public int getWidthOfReferenceTile() {
        return mWidthOfReferenceTile;
    }

    public int getVerticalOffset() {
        return mVerticalOffset;
    }

    public int getHorizontalOffset() {
        return mHorizontalOffset;
    }

    public int getYSize() {
        return mYSize;
    }

    public int getXSize() {
        return mXSize;
    }
}
