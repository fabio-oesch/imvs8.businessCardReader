package testing;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class GetXMLAttributes {

	/**
	 * This method reads the tesseract HTML and gets all the XML span tags. The
	 * result will be saved in the ArrayList which uses the DivAttribute class.
	 * When creating a new instance of DivAttribute tesseract only has
	 * dimensions so the last attribute needs to be "true".
	 * 
	 * @param fileName
	 *            insert the path to the tesseract html file
	 * @return an ArrayList of DivAttributes
	 */
	private ArrayList<TesseractAttributes> readTesseractHTML(String fileName) {
		ArrayList<TesseractAttributes> tesseractAttribute = new ArrayList<>();
		Document document;
		DocumentBuilder documentBuilder;
		DocumentBuilderFactory documentBuilderFactory;

		NodeList nodeInformationList;

		File xmlInputFile;

		try {
			xmlInputFile = new File(fileName);
			documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			document = documentBuilder.parse(xmlInputFile);

			nodeInformationList = document.getElementsByTagName("span");

			document.getDocumentElement().normalize();

			for (int index = 0; index < nodeInformationList.getLength(); index++) {
				Node nodeInformation = nodeInformationList.item(index);
				if (nodeInformation.getNodeType() == Node.ELEMENT_NODE) {
					Element elementInformation = (Element) nodeInformation;
					if (!elementInformation.getAttribute("id").contains("line")) {

						String[] bBox = elementInformation
								.getAttribute("title").split(" ");

						tesseractAttribute.add(new TesseractAttributes(
								elementInformation.getTextContent(), bBox[1],
								bBox[2], bBox[3], bBox[4]));
					}

				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return tesseractAttribute;
	}

	/**
	 * This method reads the XMLFile of the scanner and gets multiple XML tags
	 * (list, ocrField, boundingBox). The result will be saved in the ArrayList
	 * which uses the DivAttribute class. When creating a new instance of
	 * DivAttribute the scanner has no dimensions for the lower right one and
	 * uses height and width so the last attribute needs to be "false".
	 * 
	 * @param tesseractFileName
	 *            insert the path to the XML file
	 * @return a Hashmap which has the nameTag as Key and Location and Info as
	 *         Value
	 */
	public ArrayList<ScannerAttributes> readScannerXML(String scannerFileName,
			String tesseractFileName) {
		GetXMLAttributes xml = new GetXMLAttributes();
		ArrayList<TesseractAttributes> tesseractAttribute = xml
				.readTesseractHTML(tesseractFileName);
		ArrayList<ScannerAttributes> scannerAttribute = new ArrayList<>();

		Document document;
		DocumentBuilder documentBuilder;
		DocumentBuilderFactory documentBuilderFactory;

		NodeList nodeLabelList;
		NodeList nodeOcrFieldList;
		NodeList nodeBBoxList;

		File xmlInputFile;

		try {
			xmlInputFile = new File(scannerFileName);
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

					scannerAttribute.add(new ScannerAttributes(elementOcrField
							.getAttribute("text"), elementLabel
							.getAttribute("fieldName"), elementBBox
							.getAttribute("x"), elementBBox.getAttribute("y"),
							elementBBox.getAttribute("width"), elementBBox
									.getAttribute("height")));

					for (int i = 0; i < tesseractAttribute.size(); i++) {
						scannerAttribute.get(index).addTesseractBox(
								tesseractAttribute.get(i));
					}

				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return scannerAttribute;
	}
}