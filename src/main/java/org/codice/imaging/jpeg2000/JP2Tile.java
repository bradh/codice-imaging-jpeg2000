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
public class JP2Tile {

    private int mTileIndex = 0;
    private short mTilePartIndex = 0;
    private short mNumberOfTileParts = 0;
    private byte[] mData = null;

    public int getTileIndex() {
        return mTileIndex;
    }

    public short getTilePartIndex() {
        return mTilePartIndex;
    }

    public short getNumberOfTileParts() {
        return mNumberOfTileParts;
    }

    public byte[] getData() {
        return mData;
    }

    void setTileIndex(int tileIndex) {
        mTileIndex = tileIndex;
    }

    void setTilePartIndex(byte tilePartIndex) {
        mTilePartIndex = tilePartIndex;
    }

    void setNumberofTileParts(byte numberOfTileParts) {
        mNumberOfTileParts = numberOfTileParts;
    }

    void setData(byte[] bytes) {
        mData = bytes;
    }

}
