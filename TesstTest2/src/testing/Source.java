package testing;

import java.io.File;
import java.io.FileNotFoundException;

import net.sourceforge.tess4j.Tesseract;
import ch.fhnw.imvs8.businesscardreader.imagefilters.GenericFilterBundle;
import ch.fhnw.imvs8.businesscardreader.imagefilters.GrayScaleFilter;
import ch.fhnw.imvs8.businesscardreader.ocr.AnalysisResult;
import ch.fhnw.imvs8.businesscardreader.ocr.OCREngine;

public class Source {

	public static void main(String[] args) throws FileNotFoundException {
		File scannerImage = new File(
				"/School/Projekt/testdata/business-cards/christophe.meili@jaree.com/solution/scan_2013-10-02_12-15-55-image-preprocessed.png");

		File scannerFile = new File(
				"/School/Projekt/testdata/business-cards/christophe.meili@jaree.com/solution/scan_2013-10-02_12-15-55.xml");

		Tesseract instance = Tesseract.getInstance(); // JNA Interface Mapping
		// Tesseract1 instance = new Tesseract1(); // JNA Direct Mapping

		// try {
		// String result = instance.doOCR(myImage);
		// System.out.println(result);
		// } catch (TesseractException e) {
		// System.err.println(e.getMessage());
		// }

		// final String scannerFileName =
		// "/School/Projekt/testdata/business-cards/christophe.meili@jaree.com/solution/scan_2013-10-02_12-15-55.xml";
		// final String tesseractFileName =
		// "/School/Projekt/testdata/business-cards/christophe.meili@jaree.com/solution/output.html";

		GenericFilterBundle filters = new GenericFilterBundle();
		filters.appendFilter(new GrayScaleFilter());
		OCREngine engine = new OCREngine(filters);
		AnalysisResult analysisResult = engine.analyzeImage(scannerImage);
		// XMLTest test = new XMLTest(scannerFile, analysisResult,
		// "christophe meili", );
	}
}