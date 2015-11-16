/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codice.imaging.jpeg2000;

/**
 *
 * @author bradh
 */
public interface JP2Reader {

    public void skipBytes(final int numOfBytesToSkip) throws JP2ParsingException;

    public int readUnsignedInt() throws JP2ParsingException;

    public void verifyBoxType(final int boxTypeSignature) throws JP2ParsingException;

    public String getFixedLengthString(final int stringLength) throws JP2ParsingException;
}
