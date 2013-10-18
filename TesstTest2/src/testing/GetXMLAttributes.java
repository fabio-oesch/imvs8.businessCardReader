package testing;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ch.fhnw.imvs8.businesscardreader.ocr.AnalysisResult;

/**
 * This is where the magic happens!
 * 
 * @author O Lry
 * 
 */
public final class GetXMLAttributes {

	/**
	 * This method gets an analysisResult object and returns an array list of
	 * all attributes of the analyisResult object
	 * 
	 * @param analysisResult
	 *            get Object analysisResult of tesseract scan
	 * @return an arraylist of tesseractAttributes
	 */
	private static ArrayList<TesseractAttributes> getAnalysisResult(
			AnalysisResult analysisResult) {

		// creates new array list which will hold all the tesseract attributes
		ArrayList<TesseractAttributes> tesseractAttribute = new ArrayList<>();
		for (int index = 0; index < analysisResult.getResultSize(); index++) {
			// create a rectange object with the bounding boxes
			Rectangle boundingBox = analysisResult.getBoundingBox(index);
			// create new objects
			tesseractAttribute.add(new TesseractAttributes(analysisResult
					.getWord(index), boundingBox.x, boundingBox.y,
					boundingBox.width, boundingBox.height));
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
	public ArrayList<ScannerAttributes> readScannerXML(File xmlInputFile,
			AnalysisResult analysisResult) {
		// get the tesseract HTML attributes
		ArrayList<TesseractAttributes> tesseractAttribute = getAnalysisResult(analysisResult);

		// create new array list which where the scanner attributes will be
		// saved in
		ArrayList<ScannerAttributes> scannerAttribute = new ArrayList<>();

		Document document;
		DocumentBuilder documentBuilder;
		DocumentBuilderFactory documentBuilderFactory;

		NodeList nodeLabelList;
		NodeList nodeOcrFieldList;
		NodeList nodeBBoxList;

		try {
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