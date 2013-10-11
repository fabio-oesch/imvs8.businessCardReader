package testing;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.junit.Before;

public class XMLUTest extends TestCase {

	ArrayList<ScannerAttributes> xMLScanner;

	@Override
	@Before
	public void setUp() throws Exception {
		final String scannerFileName = "/School/Projekt/testdata/business-cards/christophe.meili@jaree.com/solution/scan_2013-10-02_12-15-55.xml";
		final String tesseractFileName = "/School/Projekt/testdata/business-cards/christophe.meili@jaree.com/solution/output.html";

		xMLScanner = new GetXMLAttributes().readScannerXML(scannerFileName,
				tesseractFileName);
	}

	public void testTextMatch() {
		for (int i = 0; i < xMLScanner.size(); i++) {
			textMatch(i);
		}
	}

	public void textMatch(int i) {
		StringBuilder tesseractString = new StringBuilder();
		for (int j = 0; j < xMLScanner.get(i).getTessAtts().size(); j++) {
			tesseractString.append(xMLScanner.get(i).getTessAtts().get(j));
		}
		assertEquals(tesseractString, xMLScanner.get(i).getAttributeText()
				.replace(" ", ""));
	}

}
