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
import java.io.RandomAccessFile;
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

}
