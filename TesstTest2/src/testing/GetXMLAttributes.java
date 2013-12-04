package testing;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

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
	// has the count of tesseract bounding boxes which are at the same place
	private int falsePositive;

	/**
	 * This method gets an analysisResult object and returns an array list of all attributes of the
	 * analyisResult object
	 * 
	 * @param analysisResult
	 *            get Object analysisResult of tesseract scan
	 * @return an arraylist of tesseractAttributes
	 */
	private static ArrayList<TesseractAttributes> getAnalysisResult(AnalysisResult analysisResult) {

		// creates new array list which will hold all the tesseract attributes
		ArrayList<TesseractAttributes> tesseractAttribute = new ArrayList<>();
		for (int index = 0; index < analysisResult.getResultSize(); index++) {
			// create a rectange object with the bounding boxes
			Rectangle boundingBox = analysisResult.getBoundingBox(index);
			// create new objects
			tesseractAttribute.add(new TesseractAttributes(analysisResult.getWord(index), boundingBox.x,
					boundingBox.y, boundingBox.width, boundingBox.height));
		}

		return tesseractAttribute;
	}

	/**
	 * reads the XML file from the scanner and creates a new scanner attribute. After all the scanner
	 * attributes have been scanned we are trying to calculate the scale factor and the offset of the
	 * tesseract picture to the scanner picture.
	 * 
	 * @param xmlInputFile
	 *            A file with the location to the xml file
	 * @param analysisResult
	 *            Result of the tesseract output
	 * @return return an ArrayList of ScannerAttributes. In this ArrayList the Tesseract Attributes are
	 *         matched with bounding boxes. Every ScannerAttribute has multiple TesseractAttributes which fit
	 *         to the bounding box
	 * @throws UnsupportedEncodingException
	 */
	public ArrayList<ScannerAttributes> readScannerXML(File xmlInputFile, AnalysisResult analysisResult)
			throws UnsupportedEncodingException {
		// get the tesseract HTML attributes
		ArrayList<TesseractAttributes> tesseractAttribute = getAnalysisResult(analysisResult);

		// create new array list which where the scanner attributes will be
		// saved in
		ArrayList<ScannerAttributes> scannerAttribute = new ArrayList<>();

		// Create a XMLInputFactory which will read the xml-taggs
		XMLInputFactory inputFactor = XMLInputFactory.newInstance();
		try {
			// Create a reader with the ANSI Encoding
			XMLStreamReader reader = inputFactor.createXMLStreamReader(new InputStreamReader(
					new FileInputStream(xmlInputFile), "ISO-8859-1"));
			// Save text and fieldName in a String
			String text = null;
			String fieldName = null;

			// goes through every element
			while (reader.hasNext()) {
				// only need to check if the current element is a StartingTag
				if (reader.isStartElement()) {
					if (reader.getLocalName() == "list") {
						fieldName = reader.getAttributeValue(0);
					} else if (reader.getLocalName() == "ocrField") {
						text = reader.getAttributeValue(0);
					} else if (reader.getLocalName() == "boundingBox") {
						ScannerAttributes scanAtt = new ScannerAttributes(text, fieldName,
								reader.getAttributeValue(0), reader.getAttributeValue(1),
								reader.getAttributeValue(2), reader.getAttributeValue(3));
						if (!scannerAttribute.contains(scanAtt)) {
							scannerAttribute.add(scanAtt);
						}
					}
				}
				reader.next();
			}
		} catch (FileNotFoundException e) {
			System.out.println(xmlInputFile.getAbsolutePath());
			e.printStackTrace();
		} catch (XMLStreamException e) {
			System.out.println(xmlInputFile.getAbsolutePath());
			e.printStackTrace();
		}

		// Calculate unique attributes and get the offset and scale
		getUniqueAttributes(tesseractAttribute, scannerAttribute);

		// Hashmap checks if a scannerAttribute has already been tested.
		HashMap<String, Boolean> boundingBox = new HashMap<>();
		falsePositive = 0;
		for (int i = 0; i < scannerAttribute.size(); i++) {
			for (int j = 0; j < tesseractAttribute.size(); j++) {
				if (scannerAttribute.get(i).addTesseractBox(tesseractAttribute.get(j))) {
					String key = tesseractAttribute.get(j).getX() + " " + tesseractAttribute.get(j).getY()
							+ " " + tesseractAttribute.get(j).getWidth() + " "
							+ tesseractAttribute.get(j).getHeight();
					if (!boundingBox.containsKey(key)) {
						boundingBox.put(key, true);
						// at this point returns all bounding boxes which are
						// the same.
						falsePositive++;
					}
				}
			}
		}
		// get all bounding boxes which are not a scanner attribute
		falsePositive = tesseractAttribute.size() - falsePositive;

		return scannerAttribute;
	}

	/**
	 * gets the unique attributes from the tesseract Attributes List
	 * 
	 * @param tesseractAttributes
	 *            all the tesseract Attributes of the picture
	 * @param scannerAttributes
	 *            all the scanner attributes of the picture
	 */
	public void getUniqueAttributes(ArrayList<TesseractAttributes> tesseractAttributes,
			ArrayList<ScannerAttributes> scannerAttributes) {

		ArrayList<TesseractAttributes> uniqueTesseractAttributes = new ArrayList<>();
		ArrayList<ScannerAttributes> uniqueScannerAttributes = new ArrayList<>();

		// when an attribute of the scanner contains a tesseract attribute then
		// the flag exists will be changed to true. If this flag has already
		// been set the unique flag will change to false because we have got
		// more than one of the same attributes.
		for (int i = 0; i < tesseractAttributes.size(); i++) {
			boolean exists = false;
			boolean unique = true;
			int foundAttribute = 0;
			for (int j = 0; j < scannerAttributes.size(); j++) {
				if (scannerAttributes.get(j).getAttributeText()
						.contains(tesseractAttributes.get(i).getAttributeText())
						&& unique) {
					if (!exists) {
						exists = true;
						foundAttribute = j;
						// Bugfix if multiple occurences of the tesseractAttribute in the scannerAttribute are
						if (scannerAttributes.get(j).getAttributeText()
								.indexOf(tesseractAttributes.get(i).getAttributeText()) != scannerAttributes
								.get(j).getAttributeText()
								.lastIndexOf(tesseractAttributes.get(i).getAttributeText())) {
							unique = false;
						}
					} else {
						unique = false;
					}
				}
			}
			// if an attribute has been found and it only exist once, we need to
			// check if the attribute starts with this text. Then we add it to
			// both lists
			if (exists && unique) {
				if (scannerAttributes.get(foundAttribute).getAttributeText()
						.startsWith(tesseractAttributes.get(i).getAttributeText())) {
					uniqueScannerAttributes.add(scannerAttributes.get(foundAttribute));
					uniqueTesseractAttributes.add(tesseractAttributes.get(i));
				}
			}
		}
		// calculate offset and scale for the tesseract picture
		calculate(uniqueTesseractAttributes, uniqueScannerAttributes, scannerAttributes);
	}

	/**
	 * Calculates the offset and ratio of the picture which was taken from the Mobile Camera to the picture
	 * which was scanned by the Scanner.
	 * 
	 * @param uniqueTesseractAttributes
	 *            List of attributes which only exists in the business card once
	 * @param uniqueScannerAttributes
	 *            List of attributes which only exists in the business card once
	 * @param scannerAttributes
	 *            the original attributes which come from the xml file of the scanner
	 */
	public void calculate(ArrayList<TesseractAttributes> uniqueTesseractAttributes,
			ArrayList<ScannerAttributes> uniqueScannerAttributes,
			ArrayList<ScannerAttributes> scannerAttributes) {
		// offset by X and Y axis
		double offsetX = 0;
		double offsetY = 0;
		// euclidian distance
		double euclid = 0;
		// divide by the amount of comparisons
		double counter = 0;

		// for every scannerAttribute the ratio to the different elements is
		// measured. Same for the tesseractAttributes
		for (int i = 0; i < uniqueScannerAttributes.size(); i++) {
			for (int j = i + 1; j < uniqueScannerAttributes.size(); j++) {
				double distScannerX = uniqueScannerAttributes.get(i).getX()
						- uniqueScannerAttributes.get(j).getX();
				double distScannerY = uniqueScannerAttributes.get(i).getY()
						- uniqueScannerAttributes.get(j).getY();
				double distTesseractX = uniqueTesseractAttributes.get(i).getX()
						- uniqueTesseractAttributes.get(j).getX();
				double distTesseractY = uniqueTesseractAttributes.get(i).getY()
						- uniqueTesseractAttributes.get(j).getY();
				// Euclidian Distance
				euclid += Math.sqrt(distScannerX * distScannerX + distScannerY * distScannerY)
						/ Math.sqrt(distTesseractX * distTesseractX + distTesseractY * distTesseractY);
				counter++;
			}
		}
		euclid /= counter;

		// Calculate the offset for X and Y
		for (int i = 0; i < uniqueTesseractAttributes.size(); i++) {
			offsetX += Math.abs((uniqueTesseractAttributes.get(i).getX() * euclid)
					- uniqueScannerAttributes.get(i).getX());
			offsetY += Math.abs((uniqueTesseractAttributes.get(i).getY() * euclid)
					- uniqueScannerAttributes.get(i).getY());
		}
		offsetX /= uniqueTesseractAttributes.size();
		offsetY /= uniqueTesseractAttributes.size();

		// Add Scale and Offset to every scanner Attribute. The reason
		// setTesseractCorrection has 2 times euclidian is that if we would find
		// a better scale measurement for x and y the set method doesn't need to
		// be changed
		for (int i = 0; i < scannerAttributes.size(); i++) {
			scannerAttributes.get(i).setTesseractCorrection(euclid, euclid, offsetX, offsetY);
		}
	}

	public int getFalsePositive() {
		return falsePositive;
	}
}