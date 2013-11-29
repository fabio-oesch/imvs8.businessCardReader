package testing;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import ch.fhnw.imvs8.businesscardreader.ocr.AnalysisResult;

/**
 * This is where the magic happens!
 * 
 * @author O Lry
 * 
 */
public final class GetXMLAttributes {

	public String[] uniqueAttributes;

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
	 * @throws UnsupportedEncodingException
	 */
	public ArrayList<ScannerAttributes> readScannerXML(File xmlInputFile,
			AnalysisResult analysisResult) throws UnsupportedEncodingException {
		// get the tesseract HTML attributes
		ArrayList<TesseractAttributes> tesseractAttribute = getAnalysisResult(analysisResult);

		// create new array list which where the scanner attributes will be
		// saved in
		ArrayList<ScannerAttributes> scannerAttribute = new ArrayList<>();

		XMLInputFactory inputFactor = XMLInputFactory.newInstance();
		try {
			XMLStreamReader reader = inputFactor
					.createXMLStreamReader(new InputStreamReader(
							new FileInputStream(xmlInputFile), "ISO-8859-1"));
			String text = null;
			String fieldName = null;

			while (reader.hasNext()) {
				if (reader.isStartElement()) {
					if (reader.getLocalName() == "list") {
						fieldName = reader.getAttributeValue(0);
					} else if (reader.getLocalName() == "ocrField") {
						text = reader.getAttributeValue(0);
					} else if (reader.getLocalName() == "boundingBox") {
						scannerAttribute.add(new ScannerAttributes(text,
								fieldName, reader.getAttributeValue(0), reader
										.getAttributeValue(1), reader
										.getAttributeValue(2), reader
										.getAttributeValue(3)));
					}
				}
				reader.next();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}

		getUniqueAttributes(tesseractAttribute, scannerAttribute);
		for (int i = 0; i < scannerAttribute.size(); i++) {
			for (int j = 0; j < tesseractAttribute.size(); j++) {
				scannerAttribute.get(i).addTesseractBox(
						tesseractAttribute.get(j));
			}
		}

		return scannerAttribute;
	}

	public void getUniqueAttributes(
			ArrayList<TesseractAttributes> tesseractAttributes,
			ArrayList<ScannerAttributes> scannerAttributes) {

		ArrayList<TesseractAttributes> uniqueTesseractAttributes = new ArrayList<>();
		ArrayList<ScannerAttributes> uniqueScannerAttributes = new ArrayList<>();

		for (int i = 0; i < tesseractAttributes.size(); i++) {
			boolean exists = false;
			boolean unique = true;
			int foundAttribute = 0;
			for (int j = 0; j < scannerAttributes.size(); j++) {
				if (scannerAttributes
						.get(j)
						.getAttributeText()
						.contains(tesseractAttributes.get(i).getAttributeText())
						&& unique) {
					if (!exists) {
						exists = true;
						foundAttribute = j;
					} else {
						unique = false;
					}
				}
			}
			if (exists && unique) {
				if (scannerAttributes
						.get(foundAttribute)
						.getAttributeText()
						.startsWith(
								tesseractAttributes.get(i).getAttributeText())) {
					uniqueScannerAttributes.add(scannerAttributes
							.get(foundAttribute));
					uniqueTesseractAttributes.add(tesseractAttributes.get(i));
				}
			}
		}
		calculate(uniqueTesseractAttributes, uniqueScannerAttributes,
				scannerAttributes);
	}

	public void calculate(
			ArrayList<TesseractAttributes> uniqueTesseractAttributes,
			ArrayList<ScannerAttributes> uniqueScannerAttributes,
			ArrayList<ScannerAttributes> scannerAttributes) {
		double offsetX = 0;
		double offsetY = 0;
		double euclid = 0;
		double counter = 0;

		for (int i = 0; i < uniqueTesseractAttributes.size(); i++) {
			for (int j = i + 1; j < uniqueScannerAttributes.size(); j++) {
				double distScannerX = uniqueScannerAttributes.get(i).getX()
						- uniqueScannerAttributes.get(j).getX();
				double distScannerY = uniqueScannerAttributes.get(i).getY()
						- uniqueScannerAttributes.get(j).getY();
				double distTesseractX = uniqueTesseractAttributes.get(i).getX()
						- uniqueTesseractAttributes.get(j).getX();
				double distTesseractY = uniqueTesseractAttributes.get(i).getY()
						- uniqueTesseractAttributes.get(j).getY();
				euclid += Math.sqrt(distScannerX * distScannerX + distScannerY
						* distScannerY)
						/ Math.sqrt(distTesseractX * distTesseractX
								+ distTesseractY * distTesseractY);
				counter++;
			}
		}
		euclid /= counter;

		for (int i = 0; i < uniqueTesseractAttributes.size(); i++) {
			offsetX += Math
					.abs((uniqueTesseractAttributes.get(i).getX() * euclid)
							- uniqueScannerAttributes.get(i).getX());
			offsetY += Math
					.abs((uniqueTesseractAttributes.get(i).getY() * euclid)
							- uniqueScannerAttributes.get(i).getY());
		}
		offsetX /= uniqueTesseractAttributes.size();
		offsetY /= uniqueTesseractAttributes.size();

		for (int i = 0; i < scannerAttributes.size(); i++) {
			scannerAttributes.get(i).setTesseractCorrection(euclid, euclid,
					offsetX, offsetY);
		}
	}
}