package testing;

import java.util.ArrayList;

import ch.fhnw.imvs8.businesscardreader.ocr.AnalysisResult;

/**
 * Tests if the scanner XML and the tesseract HTML file have the same attributes
 * 
 * @author O Lry
 * 
 */
public class XMLTest {

	// the XML data structure which has the xml attributes of the tesseract file
	// and attributes of the scanner file
	private ArrayList<ScannerAttributes> xMLScanner;
	// name of test
	private String testName;
	// count of errors
	private int error = 0;

	/**
	 * XMLTest gets the attributes of the files through GetXMLAttributes and
	 * matches the strings to each other
	 * 
	 * @param scannerFileName
	 *            the location of the file where the scanner xml is
	 * @param tesseractFileName
	 *            the location of the file where the tesseract html is
	 * @param testName
	 *            name of the test
	 */
	public XMLTest(String scannerFileName, String tesseractFileName,
			String testName) {

		this.testName = testName;
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
	 * XMLTest gets the attributes of the files through GetXMLAttributes and
	 * matches the strings to each other
	 * 
	 * @param scannerFileName
	 *            the location of the file where the scanner xml is
	 * @param analysisResult
	 *            analysisResult object of the file
	 * @param testName
	 *            name of the test
	 */
	public XMLTest(String scannerFileName, AnalysisResult analysisResult,
			String testName) {

		this.testName = testName;
		System.out
				.println("------------- Getting XML Attributes -------------");
		double time = System.currentTimeMillis();

		// Get all the XML Attributes
		xMLScanner = new GetXMLAttributes()
				.readScannerXMLWithAnalysisResultObject(scannerFileName,
						analysisResult);
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
		System.out.println("-------------- " + testName + " ----------------");
		for (int i = 0; i < xMLScanner.size(); i++) {
			textMatch(i);
		}
		System.out.println("Count of errors: " + error);
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
			error++;
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
