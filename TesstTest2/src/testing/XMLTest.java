package testing;

import java.io.File;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class XMLTest {

	private static HashMap<String, DivAttribute> scannerAttribute = new HashMap<>();

	public static void main(String[] args) {
		final String scannerFileName = "/School/Projekt/testdata/business-cards/christophe.meili@jaree.com/solution/scan_2013-10-02_12-15-55.xml";

		readScannerXML(scannerFileName);
	}

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
									.getAttribute("height")));

				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private static void printHashMap() {
		for (String value : scannerAttribute.keySet()) {
			System.out.println("Hashmap key: " + value + " ,value: "
					+ scannerAttribute.get(value));
		}
	}
}