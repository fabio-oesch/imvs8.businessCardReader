package testing;

import java.io.File;

import net.sourceforge.tess4j.Tesseract;

public class Source {

	public static void main(String[] args) {
		File myImage = new File(
				"/School/Projekt/testdata/business-cards/christophe.meili@jaree.com/solution/scan_2013-10-02_12-15-55-image-preprocessed.png");

		File imageFile = new File("eurotext.tif");
		Tesseract instance = Tesseract.getInstance(); // JNA Interface Mapping
		// Tesseract1 instance = new Tesseract1(); // JNA Direct Mapping

		// try {
		// String result = instance.doOCR(myImage);
		// System.out.println(result);
		// } catch (TesseractException e) {
		// System.err.println(e.getMessage());
		// }

		final String scannerFileName = "/School/Projekt/testdata/business-cards/christophe.meili@jaree.com/solution/scan_2013-10-02_12-15-55.xml";
		final String tesseractFileName = "/School/Projekt/testdata/business-cards/christophe.meili@jaree.com/solution/output.html";

		XMLTest test = new XMLTest(scannerFileName, tesseractFileName,
				"christophe meili");
	}
}