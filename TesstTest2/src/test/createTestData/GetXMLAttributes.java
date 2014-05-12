package test.createTestData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class GetXMLAttributes {

	/**
	 * reads the XML file from the scanner and creates a new scanner attribute.
	 * After all the scanner attributes have been scanned we are trying to
	 * calculate the scale factor and the offset of the tesseract picture to the
	 * scanner picture.
	 * 
	 * @param xmlInputFile
	 *            A file with the location to the xml file
	 * @param analysisResult
	 *            Result of the tesseract output
	 * @return return an ArrayList of ScannerAttributes. In this ArrayList the
	 *         Tesseract Attributes are matched with bounding boxes. Every
	 *         ScannerAttribute has multiple TesseractAttributes which fit to
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	public HashMap<String, String> readXMLAttributes(File xmlInputFile) throws NumberFormatException, IOException {

		HashMap<String, String> xmlAttributes = new HashMap<>();
		// Create a XMLInputFactory which will read the xml-taggs
		XMLInputFactory inputFactor = XMLInputFactory.newInstance();
		try {
			// Create a reader with the ANSI Encoding
			XMLStreamReader reader = inputFactor.createXMLStreamReader(new InputStreamReader(new FileInputStream(xmlInputFile), "ISO-8859-1"));
			// Save text and fieldName in a String
			String label = null;
			String fieldName = null;

			// goes through every element
			while (reader.hasNext()) {
				// only need to check if the current element is a StartingTag
				if (reader.isStartElement()) {
					if (reader.getLocalName() == "list") {
						fieldName = reader.getAttributeValue(0);
					} else if (reader.getLocalName() == "label" && !reader.getAttributeValue(0).equals("")) {
						xmlAttributes.put(reader.getAttributeValue(0), fieldName);
						label = reader.getAttributeValue(0);

					}
				}
				reader.next();
			}
		} catch (FileNotFoundException e) {
			System.out.println(xmlInputFile.getAbsolutePath());
			e.printStackTrace();
		} catch (XMLStreamException e) {
			System.out.println(xmlInputFile.getAbsolutePath());
			e.printStackTrace();
		}
		return xmlAttributes;
	}
}
