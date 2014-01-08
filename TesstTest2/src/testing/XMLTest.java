package testing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
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
	private final ArrayList<ScannerAttributes> xMLScanner;
	// count of errors
	private double error = 0;
	// count of xmlScannerAttributes
	private double countScannerAttributes = 0;
	// to write into log file
	BufferedWriter bw;

	// f measure
	private double truePositive;
	private final double falsePositive;
	private double falseNegative;
	private final int uniqueTessCount;
	private final int uniqueScannerCount;

	// f measure boundingbox
	private final double boundingboxTruePositive;
	private final double boundingboxFalsePositive;
	private double boundingboxFalseNegative;

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
	 * @throws IOException
	 */
	public XMLTest(File scannerFileName, File tesseractFileName, AnalysisResult analysisResult, BufferedWriter bw)
			throws IOException {
		this.bw = bw;

		GetXMLAttributes xmlAttributes = new GetXMLAttributes();

		// Get all the XML Attributes
		xMLScanner = xmlAttributes.readScannerXML(scannerFileName, tesseractFileName, analysisResult);

		this.uniqueScannerCount = xmlAttributes.getUniqueScannerCount();
		this.uniqueTessCount = xmlAttributes.getUniqueTesseractCount();
		falsePositive = xmlAttributes.getFalsePositive();

		// get truepositive
		boundingboxTruePositive = xmlAttributes.getBoundingboxTruePositive();
		boundingboxFalsePositive = falsePositive;

		// Test if the XML Attributes are the same
		if (xMLScanner != null) {
			testTextMatch();
		}
	}

	/**
	 * goes through all the catogeries of the XMLScanner file and checks if they
	 * are the same
	 * 
	 * @throws IOException
	 */
	private void testTextMatch() throws IOException {
		for (int i = 0; i < xMLScanner.size(); i++) {
			textMatch(i);
		}
		truePositive = xMLScanner.size() - error;
		falseNegative = error;
		countScannerAttributes = xMLScanner.size();
	}

	/**
	 * Checks if the Attributes at the location index of the Scanner file the
	 * same attribute is as the one from the tesseract file
	 * 
	 * @param scannerCategories
	 *            goes through the categories of the scanner file xml
	 * @throws IOException
	 */
	private void textMatch(int scannerCategories) throws IOException {
		StringBuilder tesseractString = new StringBuilder();
		// builds a string with the elements which are in the data structure in
		// the specific category
		for (int index = 0; index < xMLScanner.get(scannerCategories).getTessAtts().size(); index++) {
			tesseractString.append(xMLScanner.get(scannerCategories).getTessAtts().get(index).getAttributeText());
		}

		int temp = xMLScanner.get(scannerCategories).getAttributeText().split(" ").length
				- xMLScanner.get(scannerCategories).getTessAtts().size();

		if (temp > 0) {
			boundingboxFalseNegative += temp;
		}

		// check if scanner attribute (- spaces) are not the same as the
		// tesseract attributes
		if (!tesseractString.toString().equals(xMLScanner.get(scannerCategories).getAttributeText().replace(" ", ""))) {
			error++;
			// Print information about the mistake

			bw.write("# Catogory: " + xMLScanner.get(scannerCategories).getAttributeTyp() + ", tesseract Text: "
					+ tesseractString.toString() + ", scanner text: "
					+ xMLScanner.get(scannerCategories).getAttributeText().replace(" ", "") + "\n");

		}

	}

	public double getPercentageErrors() {
		return 1 - error / countScannerAttributes;
	}

	public double getErrors() {
		return error;
	}

	public double getPrecision() {
		return truePositive / (truePositive + falsePositive);
	}

	public double getRecall() {
		return truePositive / (truePositive + falseNegative);
	}

	public double f_Measure() {
		double precision = getPrecision();
		double recall = getRecall();
		if (precision == 0 && recall == 0) {
			return 0;
		}
		return 2 * (precision * recall / (precision + recall));
	}

	public double boundingboxGetPrecision() {
		return boundingboxTruePositive / (boundingboxTruePositive + boundingboxFalsePositive);
	}

	public double boundingboxGetRecall() {
		return boundingboxTruePositive / (boundingboxTruePositive + boundingboxFalseNegative);
	}

	public double boundingboxF_Measure() {
		double precision = boundingboxGetPrecision();
		double recall = boundingboxGetRecall();
		if (precision == 0 && recall == 0) {
			return 0;
		}
		return 2 * (precision * recall / (precision + recall));
	}

	public int uniqueStuff() {
		return this.uniqueScannerCount;
	}

	public ScannerAttributes getScannerAttribute() {
		return xMLScanner.get(0);
	}

}
