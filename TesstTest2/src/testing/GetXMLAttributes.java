package testing;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This is where the magic happens!
 * 
 * @author O Lry
 * 
 */
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
		// creates new array list which will hold all the tesseract attributes
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

			// receive a list of all the span-tag documents
			nodeInformationList = document.getElementsByTagName("span");

			document.getDocumentElement().normalize();

			// loops through all the nodes of the List
			for (int index = 0; index < nodeInformationList.getLength(); index++) {
				// get current Node
				Node nodeInformation = nodeInformationList.item(index);
				if (nodeInformation.getNodeType() == Node.ELEMENT_NODE) {
					Element elementInformation = (Element) nodeInformation;

					// if the current span element has an attribute id which is
					// called line it is not usefull because it tells which line
					// we are currently on
					if (!elementInformation.getAttribute("id").contains("line")) {

						// get the bounding boxes of the current span element
						String[] bBox = elementInformation
								.getAttribute("title").split(" ");

						// create a new TesseractAttribute with the text and the
						// 4 bounding boxes, bBox[0] is always "bBox"
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
	 * @return a ArrayList which has the nameTag as Key and Location and Info as
	 *         Value
	 */
	public ArrayList<ScannerAttributes> readScannerXML(String scannerFileName,
			String tesseractFileName) {
		// create a new instance of GetXMLAttribute to save the attributes of
		// the Tesseract HTML
		GetXMLAttributes xml = new GetXMLAttributes();
		// get the tesseract HTML attributes
		ArrayList<TesseractAttributes> tesseractAttribute = xml
				.readTesseractHTML(tesseractFileName);
		// create new array list which where the scanner attributes will be
		// saved in
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

			// get all the nodes which have either list, ocrField or boundingBox
			// as name
			nodeLabelList = document.getElementsByTagName("list");
			nodeOcrFieldList = document.getElementsByTagName("ocrField");
			nodeBBoxList = document.getElementsByTagName("boundingBox");

			document.getDocumentElement().normalize();

			// go through all the nodes of the labellist. The labellist could be
			// described as the catogery
			for (int index = 0; index < nodeLabelList.getLength(); index++) {
				// get the index item of each list
				Node nodeLabel = nodeLabelList.item(index);
				Node nodeOcrField = nodeOcrFieldList.item(index);
				Node nodeBBox = nodeBBoxList.item(index);

				if (nodeLabel.getNodeType() == Node.ELEMENT_NODE) {
					// turn them into an element
					Element elementLabel = (Element) nodeLabel;
					Element elementOcrField = (Element) nodeOcrField;
					Element elementBBox = (Element) nodeBBox;

					// add a new scannerAttribute to the to the list. The
					// attributes are being read at the same time
					scannerAttribute.add(new ScannerAttributes(elementOcrField
							.getAttribute("text"), elementLabel
							.getAttribute("fieldName"), elementBBox
							.getAttribute("x"), elementBBox.getAttribute("y"),
							elementBBox.getAttribute("width"), elementBBox
									.getAttribute("height")));

					// check if a tesseract Attributes needs to be in the
					// arraylist of the current scanner attribute. This is the
					// case if the bounding boxes are roughly at the same
					// location
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