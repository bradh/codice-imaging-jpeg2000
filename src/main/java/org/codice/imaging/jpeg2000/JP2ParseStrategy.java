/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

    private static final int BOX_SIGNATURE_FILETYPE = 0x66747970;
    private static final int BOX_SIGNATURE_LENGTH = 4;
    private static final int BRAND_STRING_LENGTH = 4;
    private static final int UNSIGNED_INT_LENGTH = 4;
    private static final int COMPATIBILITY_LIST_ENTRY_LENGTH = 4;

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
                    parseJP2HeaderBox(remainingBytesInBox);
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
        mReader.skipBytes(codestreamLength);
    }

    private void parseJP2HeaderBox(int superBoxLength) throws JP2ParsingException {
        mReader.skipBytes(superBoxLength);
    }

    private void parseXMLBox(int xmlLength) throws JP2ParsingException {
        String xml = mReader.getFixedLengthString(xmlLength);
        mXmlList.add(xml);
    }

    private void parseFileTypeBox() throws JP2ParsingException {
        int boxLength = mReader.readUnsignedInt();
        mReader.verifyBoxType(BOX_SIGNATURE_FILETYPE);
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
}
