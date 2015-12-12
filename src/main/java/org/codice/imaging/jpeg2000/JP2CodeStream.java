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
public class JP2CodeStream {

    private static final int SIZ_MARKER_CODE = 0xFF51;
    private static final int SOC_MARKER_CODE = 0xFF4F;
    private static final int SOD_MARKER_CODE = 0xFF93;
    private static final int SOT_MARKER_CODE = 0xFF90;
    private static final int COD_MARKER_CODE = 0xFF52;
    private static final int QCD_MARKER_CODE = 0xFF5C;
    private static final int EOC_MARKER_CODE = 0xFFD9;

    private JP2Reader mReader = null;
    private int mRemainingCodestreamLength = 0;

    private int mVerticalOffsetOfReferenceTile;
    private int mHorizontalOffsetOfReferenceTile;
    private int mHeightOfReferenceTile;
    private int mWidthOfReferenceTile;
    private int mVerticalOffset;
    private int mHorizontalOffset;
    private int mYSize;
    private int mXSize;
    private int mRequiredCapabilities;
    private int mNumberOfComponentsInImage;

    private final List<JP2Tile> mTiles = new ArrayList<>();
    private List<Integer> mQuantizationExponents = new ArrayList<>();

    public JP2CodeStream(JP2Reader reader, final int codestreamLength) throws JP2ParsingException {
        mReader = reader;
        mRemainingCodestreamLength = codestreamLength;
        verifyMarkerCode(SOC_MARKER_CODE);
        parseMainHeaderAndTiles();
        verifyMarkerCode(EOC_MARKER_CODE);
    }

    private void verifyMarkerCode(int expectedMarkerCode) throws JP2ParsingException {
        int actualMarkerCode = mReader.readUnsignedShort();
        mRemainingCodestreamLength -= 2;
        if (actualMarkerCode != expectedMarkerCode) {
            throw new JP2ParsingException(String.format("Missing expected marker. Expected 0x%04x but got 0x%04x", expectedMarkerCode, actualMarkerCode));
        }
    }

    private void parseMainHeaderAndTiles() throws JP2ParsingException {
        parseImageAndTileSize();
        while (mRemainingCodestreamLength > 2) {
            int markerCode = mReader.readUnsignedShort();
            mRemainingCodestreamLength -= 2;
            switch (markerCode) {
                case COD_MARKER_CODE: {
                    parseCodingStyleDefault();
                    break;
                }
                case SOT_MARKER_CODE: {
                    JP2Tile tile = parseTilePart();
                    mTiles.add(tile);
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
        verifyMarkerCode(SIZ_MARKER_CODE);
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
        mNumberOfComponentsInImage = mReader.readUnsignedShort();
        // TODO: parse this properly
        for (int i = 0; i < mNumberOfComponentsInImage; ++i) {
            mReader.skipBytes(3);
        }
    }

    private JP2Tile parseTilePart() throws JP2ParsingException {
        JP2Tile tile = new JP2Tile();
        int markerLength = mReader.readUnsignedShort();
        if (markerLength != (2 * PackageConstants.UNSIGNED_SHORT_LENGTH + PackageConstants.UNSIGNED_INT_LENGTH + 2 * PackageConstants.UNSIGNED_BYTE_LENGTH)) {
            throw new JP2ParsingException("Invalid length for SOT part:" + markerLength);
        }
        tile.setTileIndex(mReader.readUnsignedShort());
        int psot = mReader.readUnsignedInt();
        tile.setTilePartIndex(mReader.readUnsignedByte());
        tile.setNumberofTileParts(mReader.readUnsignedByte());
        mRemainingCodestreamLength -= markerLength;
        int expectedStartOfDataMarker = mReader.readUnsignedShort();
        mRemainingCodestreamLength -= 2;
        if (expectedStartOfDataMarker != SOD_MARKER_CODE) {
            throw new JP2ParsingException("Missing expected SOF marker");
        }
        int tileBitstreamLength = psot - (PackageConstants.UNSIGNED_SHORT_LENGTH + markerLength + PackageConstants.UNSIGNED_SHORT_LENGTH);
        tile.setData(mReader.getBytes(tileBitstreamLength));
        mRemainingCodestreamLength -= tileBitstreamLength;
        return tile;
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
        mRemainingCodestreamLength -= markerLength;
    }

    private void parseQuantizationDefault() throws JP2ParsingException {
        int markerLength = mReader.readUnsignedShort();
        int quantizationStyleForAllComponents = mReader.readUnsignedByte();
        if ((quantizationStyleForAllComponents & 0x1F) == 0) {
            // No quantization
            int numberOfQuantizationSteps = (markerLength - 3);
            for (int i = 0; i < numberOfQuantizationSteps; ++i) {
                int quantizationStepValue = mReader.readUnsignedByte() >> 3;
                mQuantizationExponents.add(quantizationStepValue);
            }
        } else {
            // TODO: Handle scalar derived and scalar expounded
            byte[] bytes = mReader.getBytes(markerLength - 3);
        }
        int numberOfGuardBits = quantizationStyleForAllComponents >> 5;
        mRemainingCodestreamLength -= markerLength;
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

    public List<JP2Tile> getTiles() {
        return mTiles;
    }

    public int getNumberOfComponentsInImage() {
        return mNumberOfComponentsInImage;
    }

}
