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

import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author bradh
 */
public class TestBasicJP2Parsing {

    private static final String xml1 =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r" +
        "<IMAGE_CREATION xmlns=\"http://www.jpeg.org/jpx/1.0/xml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.jpeg.org/jpx/1.0/xml\r" +
        "http://www.jpeg.org/metadata/15444-2.xsd\">\r" +
        "	<GENERAL_CREATION_INFO>\r" +
        "		<CREATION_TIME>2001-11-01T13:45:00.000-06:00</CREATION_TIME>\r" +
        "		<IMAGE_SOURCE>Professional 120 Image</IMAGE_SOURCE>\r" +
        "	</GENERAL_CREATION_INFO>\r" +
        "</IMAGE_CREATION>\r\r";

    private static final String xml2 =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r" +
        "<CONTENT_DESCRIPTION xmlns=\"http://www.jpeg.org/jpx/1.0/xml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.jpeg.org/jpx/1.0/xml\r" +
        " http://www.jpeg.org/metadata/15444-2.xsd\">\r" +
        "	<CAPTION>Houchin Castle</CAPTION>\r" +
        "	<LOCATION>\r" +
        "		<ADDRESS TYPE=\"Scene Address Location\">\r" +
        "			<ADDR_COMP TYPE=\"Street\">269 Castle Street</ADDR_COMP>\r" +
        "			<ADDR_COMP TYPE=\"City\">Greece</ADDR_COMP>\r" +
        "			<ADDR_COMP TYPE=\"State\">New York</ADDR_COMP>\r" +
        "			<ZIPCODE>14059</ZIPCODE>\r" +
        "			<COUNTRY>US</COUNTRY>\r" +
        "		</ADDRESS>\r" +
        "	</LOCATION>\r" +
        "	<EVENT>\r" +
        "		<EVENT_TYPE>Moving Day</EVENT_TYPE>\r" +
        "		<DESCRIPTION>Scott's new Castle</DESCRIPTION>\r" +
        "		<COMMENT>Color scientists rule</COMMENT>\r" +
        "	</EVENT>\r" +
        "</CONTENT_DESCRIPTION>\r" +
        "\r";

    public TestBasicJP2Parsing() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testFile1() throws JP2ParsingException {
        final String testfilename = "/file1.jp2";
        File testfile = new File(getClass().getResource(testfilename).getFile());

        assertNotNull("Test file missing", testfile);

        JP2ParseStrategy parseStrategy = new JP2ParseStrategy();
        JP2Reader reader = new JP2FileReader(testfile);
        JP2Parser.parse(reader, parseStrategy);
        assertEquals("jp2 ", parseStrategy.getFileTypeBox().getBranding());
        assertEquals(0, parseStrategy.getFileTypeBox().getMinorVersion());
        assertEquals(1, parseStrategy.getFileTypeBox().getCompatibilityList().size());
        assertTrue(parseStrategy.getFileTypeBox().getCompatibilityList().contains("jp2 "));

        assertEquals(2, parseStrategy.getXmlList().size());
        assertEquals(xml1, parseStrategy.getXmlList().get(0).getXml());
        assertEquals(xml2, parseStrategy.getXmlList().get(1).getXml());

        assertEquals(768, parseStrategy.getImageWidth());
        assertEquals(512, parseStrategy.getImageHeight());
        assertEquals(3, parseStrategy.getNumberOfComponents());
        assertEquals(7, parseStrategy.getBitsPerComponent());
        assertFalse(parseStrategy.hasUnknownColourspace());
        assertFalse(parseStrategy.hasIntellectualPropertyRights());

        assertNotNull(parseStrategy.getColourSpecification());
        assertEquals(JP2ColourSpecificationBox.ENUM_COLOUR_SPACE_SRGB, parseStrategy.getColourSpecification().getColourSpace());

        assertNull(parseStrategy.getChannelDefinitionBox());

        assertNotNull(parseStrategy.getCodeStream());
    }

    @Test
    public void testFile2() throws JP2ParsingException {
        final String testfilename = "/file2.jp2";
        File testfile = new File(getClass().getResource(testfilename).getFile());

        assertNotNull("Test file missing", testfile);

        JP2ParseStrategy parseStrategy = new JP2ParseStrategy();
        JP2Reader reader = new JP2FileReader(testfile);
        JP2Parser.parse(reader, parseStrategy);
        assertEquals("jp2 ", parseStrategy.getFileTypeBox().getBranding());
        assertEquals(0, parseStrategy.getFileTypeBox().getMinorVersion());
        assertEquals(1, parseStrategy.getFileTypeBox().getCompatibilityList().size());
        assertTrue(parseStrategy.getFileTypeBox().getCompatibilityList().contains("jp2 "));

        assertNotNull(parseStrategy.getChannelDefinitionBox());
        assertEquals(3, parseStrategy.getChannelDefinitionBox().getNumberOfEntries());
        assertNotNull(parseStrategy.getChannelDefinitionBox().getEntry(0));
        assertEquals(0, parseStrategy.getChannelDefinitionBox().getEntry(0).getChannelIndex());
        assertEquals(0, parseStrategy.getChannelDefinitionBox().getEntry(0).getChannelType());
        assertEquals(3, parseStrategy.getChannelDefinitionBox().getEntry(0).getChannelAssociation());
        assertEquals(1, parseStrategy.getChannelDefinitionBox().getEntry(1).getChannelIndex());
        assertEquals(0, parseStrategy.getChannelDefinitionBox().getEntry(1).getChannelType());
        assertEquals(2, parseStrategy.getChannelDefinitionBox().getEntry(1).getChannelAssociation());
        assertEquals(2, parseStrategy.getChannelDefinitionBox().getEntry(2).getChannelIndex());
        assertEquals(0, parseStrategy.getChannelDefinitionBox().getEntry(2).getChannelType());
        assertEquals(1, parseStrategy.getChannelDefinitionBox().getEntry(2).getChannelAssociation());
    }
}
