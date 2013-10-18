package testing;

import java.util.ArrayList;

/**
 * Tests if the scanner XML and the tesseract HTML file have the same attributes
 * 
 * @author O Lry
 * 
 */
public class XMLTest {

	ArrayList<ScannerAttributes> xMLScanner; // the XML data structure which has
												// the xml attributes of the
												// tesseract file and
												// attributes of the scanner
												// file

	/**
	 * XMLTest gets the attributes of the files through GetXMLAttributes and
	 * matches the strings to each other
	 * 
	 * @param scannerFileName
	 *            the location of the file where the scanner xml is
	 * @param tesseractFileName
	 *            the location of the file where the tesseract html is
	 */
	public XMLTest(String scannerFileName, String tesseractFileName) {

		System.out
				.println("------------- Getting XML Attributes -------------");
		double time = System.currentTimeMillis();

		// Get all the XML Attributes
		xMLScanner = new GetXMLAttributes().readScannerXML(scannerFileName,
				tesseractFileName);
		System.out.println("------------- Got XML Attributes took "
				+ (System.currentTimeMillis() - time) / 60000
				+ " min -----------------");

		// Test if the XML Attributes are the same
		testTextMatch();
	}

	/**
	 * goes through all the catogeries of the XMLScanner file and checks if they
	 * are the same
	 */
	private void testTextMatch() {
		for (int i = 0; i < xMLScanner.size(); i++) {
			textMatch(i);
		}
	}

	/**
	 * Checks if the Attributes at the location index of the Scanner file the
	 * same attribute is as the one from the tesseract file
	 * 
	 * @param scannerCategories
	 *            goes through the categories of the scanner file xml
	 */
	private void textMatch(int scannerCategories) {
		StringBuilder tesseractString = new StringBuilder();
		// builds a string with the elements which are in the data structure in
		// the specific category
		for (int index = 0; index < xMLScanner.get(scannerCategories)
				.getTessAtts().size(); index++) {
			tesseractString.append(xMLScanner.get(scannerCategories)
					.getTessAtts().get(index).getAttributeText());
		}

		// check if scanner attribute (- spaces) are not the same as the
		// tesseract attributes
		if (!(tesseractString.toString().equals(xMLScanner
				.get(scannerCategories).getAttributeText().replace(" ", "")))) {
			// Print information about the mistake
			System.out.println("Catogory: "
					+ xMLScanner.get(scannerCategories).getAttributeTyp()
					+ ", tesseract Text: "
					+ tesseractString.toString()
					+ ", scanner text: "
					+ xMLScanner.get(scannerCategories).getAttributeText()
							.replace(" ", ""));
		}
	}

}
