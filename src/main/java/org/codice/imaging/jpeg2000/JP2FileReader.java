/*
 * The MIT License
 *
 * Copyright 2015 bradh.
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author bradh
 */
public class JP2FileReader implements JP2Reader {

    static final String NOT_FOUND_MESSAGE_JOINER = " not found: ";
    static final String FILE_NOT_FOUND_EXCEPTION_MESSAGE = "File Not Found Exception opening file:";
    static final String READ_MODE = "r";

    private static final Logger LOG = LoggerFactory.getLogger(JP2FileReader.class);

    private RandomAccessFile mFile = null;

    /**
        Constructor for file-based reader.

        @param file the File to read the JP2 file contents from.
        @throws JP2ParsingException if file does not exist as a regular file, or some other error occurs during opening of the file.
    */
    public JP2FileReader(final File file) throws JP2ParsingException {
        try {
            mFile = makeRandomAccessFile(file, READ_MODE);
        } catch (FileNotFoundException ex) {
            LOG.warn(FILE_NOT_FOUND_EXCEPTION_MESSAGE + file.getPath(), ex);
            throw new JP2ParsingException(file.getPath() + NOT_FOUND_MESSAGE_JOINER +  ex.getMessage());
        }
    }

    /**
        Constructor for file-based reader.

        @param filename the String specifying the JP2 file name to read contents from.
        @throws JP2ParsingException if file does not exist as a regular file, or some other error occurs during opening of the file.
    */
    public JP2FileReader(final String filename) throws JP2ParsingException {
        try {
            mFile = makeRandomAccessFile(filename, READ_MODE);
        } catch (FileNotFoundException ex) {
            LOG.warn(FILE_NOT_FOUND_EXCEPTION_MESSAGE + filename, ex);
            throw new JP2ParsingException(filename + NOT_FOUND_MESSAGE_JOINER +  ex.getMessage());
        }
    }

    private RandomAccessFile makeRandomAccessFile(final File file, final String mode) throws FileNotFoundException {
        return new RandomAccessFile(file, mode);
    }

    private RandomAccessFile makeRandomAccessFile(final String filename, final String mode) throws FileNotFoundException {
        return new RandomAccessFile(filename, mode);
    }

    @Override
    public void skipBytes(final int numOfBytesToSkip) throws JP2ParsingException {
        try {
            mFile.skipBytes(numOfBytesToSkip);
        } catch (IOException ex) {
            LOG.warn("Could not skip " + numOfBytesToSkip + " bytes", ex);
            throw new JP2ParsingException("Could not skip bytes, " +  ex.getMessage());
        }
    }

    @Override
    public byte readUnsignedByte() throws JP2ParsingException {
        try {
            return mFile.readByte();
        } catch (IOException ex) {
            LOG.warn("Could not read byte", ex);
            throw new JP2ParsingException("Could not read byte, " +  ex.getMessage());
        }
    }

    @Override
    public int readUnsignedShort() throws JP2ParsingException {
        try {
            return (mFile.readShort() & 0x0000FFFF);
        } catch (IOException ex) {
            LOG.warn("Could not read short", ex);
            throw new JP2ParsingException("Could not read short, " +  ex.getMessage());
        }
    }

    @Override
    public int readUnsignedInt() throws JP2ParsingException {
        try {
            return mFile.readInt();
        } catch (IOException ex) {
            LOG.warn("Could not read integer", ex);
            throw new JP2ParsingException("Could not read integer, " +  ex.getMessage());
        }
    }

    @Override
    public void verifyBoxType(int boxTypeSignature) throws JP2ParsingException {
        int boxType = readUnsignedInt();
        if (boxTypeSignature != boxType) {
            LOG.warn("Box Type signature verification failure - expected " + boxTypeSignature + " but got " + boxType);
            throw new JP2ParsingException("BoxType signature verification failure");
        }
    }

    @Override
    public String getFixedLengthString(int stringLength) throws JP2ParsingException {
        try {
            byte[] stringAsBytes = new byte[stringLength];
            int numBytesRead = mFile.read(stringAsBytes);
            if (numBytesRead != stringLength) {
                throw new IOException("Unexpected number of bytes read - expected " + stringLength + ", but only got " + numBytesRead);
            }
            return new String(stringAsBytes, "US-ASCII");
        } catch (IOException ex) {
            LOG.warn("Unable to read fixed length string", ex);
            throw new JP2ParsingException("Unable to read fixed length string, exception was:" + ex.getMessage());
        }
    }

    @Override
    public byte[] getBytes(int byteArrayLength) throws JP2ParsingException {
        try {
            byte[] bytes = new byte[byteArrayLength];
            int numBytesRead = mFile.read(bytes);
            if (numBytesRead != byteArrayLength) {
                throw new IOException("Unexpected number of bytes read - expected " + byteArrayLength + ", but only got " + numBytesRead);
            }
            return bytes;
        } catch (IOException ex) {
            LOG.warn("Unable to read fixed length byte array", ex);
            throw new JP2ParsingException("Unable to read fixed length byte array, exception was:" + ex.getMessage());
        }
    }

    @Override
    public boolean hasDataRemaining() throws JP2ParsingException {
        try {
            return mFile.getFilePointer() < mFile.length();
        } catch (IOException ex) {
            LOG.warn("Unable to check if data remaining", ex);
            throw new JP2ParsingException("Unable to check if data remaining, exception was:" + ex.getMessage());
        }
    }


}
