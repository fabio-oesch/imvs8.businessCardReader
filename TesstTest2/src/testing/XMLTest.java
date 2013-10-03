package testing;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class XMLTest {

	private static HashMap<String, DivAttribute> scannerAttribute = new HashMap<>();
	private static ArrayList<DivAttribute> tesseractAttribute = new ArrayList<>();

	public static void main(String[] args) {
		final String scannerFileName = "/School/Projekt/testdata/business-cards/christophe.meili@jaree.com/solution/scan_2013-10-02_12-15-55.xml";
		final String tesseractFileName = "/School/Projekt/testdata/business-cards/christophe.meili@jaree.com/solution/output.html";
		// readScannerXML(scannerFileName);
		readTesseractHTML(tesseractFileName);
	}

	/**
	 * This method reads the tesseract HTML and gets all the XML span tags. The
	 * result will be saved in the ArrayList tesseractAttribute which uses the
	 * DivAttribute class. When creating a new instance of DivAttribute
	 * tesseract only has dimensions so the last attribute needs to be "true".
	 * 
	 * @param fileName
	 *            insert the path to the tesseract html file
	 */
	private static void readTesseractHTML(String fileName) {
		System.out.println("lulz");
		Document document;
		DocumentBuilder documentBuilder;
		DocumentBuilderFactory documentBuilderFactory;

		NodeList nodeInformationList;

		File xmlInputFile;

		try {
			xmlInputFile = new File(fileName);
			documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			System.out.println("lulz2");
			document = documentBuilder.parse(xmlInputFile);
			System.out.println("lulz3");

			nodeInformationList = document.getElementsByTagName("span");

			document.getDocumentElement().normalize();

			for (int index = 0; index < nodeInformationList.getLength(); index++) {
				Node nodeInformation = nodeInformationList.item(index);
				if (nodeInformation.getNodeType() == Node.ELEMENT_NODE) {
					Element elementInformation = (Element) nodeInformation;
					if (!elementInformation.getAttribute("id").contains("line")) {

						String[] bBox = elementInformation
								.getAttribute("title").split(" ");

						tesseractAttribute.add(new DivAttribute(
								elementInformation.getTextContent(), bBox[1],
								bBox[2], bBox[3], bBox[4], true));
					}

				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * This method reads the tesseract HTML and gets all the XML span tags. The
	 * result will be saved in the ArrayList tesseractAttribute which uses the
	 * DivAttribute class. When creating a new instance of DivAttribute
	 * tesseract only has dimensions so the last attribute needs to be "true".
	 * 
	 * @param fileName
	 *            insert the path to the XML file
	 */
	private static void readScannerXML(String fileName) {
		Document document;
		DocumentBuilder documentBuilder;
		DocumentBuilderFactory documentBuilderFactory;

		NodeList nodeLabelList;
		NodeList nodeOcrFieldList;
		NodeList nodeBBoxList;

		File xmlInputFile;

		try {
			xmlInputFile = new File(fileName);
			documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			document = documentBuilder.parse(xmlInputFile);

			nodeLabelList = document.getElementsByTagName("list");
			nodeOcrFieldList = document.getElementsByTagName("ocrField");
			nodeBBoxList = document.getElementsByTagName("boundingBox");

			document.getDocumentElement().normalize();

			for (int index = 0; index < nodeLabelList.getLength(); index++) {
				Node nodeLabel = nodeLabelList.item(index);
				Node nodeOcrField = nodeOcrFieldList.item(index);
				Node nodeBBox = nodeBBoxList.item(index);
				if (nodeLabel.getNodeType() == Node.ELEMENT_NODE) {
					Element elementLabel = (Element) nodeLabel;
					Element elementOcrField = (Element) nodeOcrField;
					Element elementBBox = (Element) nodeBBox;

					scannerAttribute.put(
							elementLabel.getAttribute("fieldName"),
							new DivAttribute(elementOcrField
									.getAttribute("text"), elementBBox
									.getAttribute("x"), elementBBox
									.getAttribute("y"), elementBBox
									.getAttribute("width"), elementBBox
									.getAttribute("height"), false));

				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}