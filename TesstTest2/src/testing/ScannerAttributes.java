package testing;

import java.util.ArrayList;

/**
 * An attribute of the scanner file. This involves an arraylist of tesseract
 * Attributes which have roughly the same bounding boxes
 * 
 * @author O Lry
 * 
 */
public class ScannerAttributes {

	private String attributeText; // text of the attribute
	private String attributeTyp; // catogery of the attribute

	private int x; // upper left x value of the bounding box
	private int y; // upper left y value of the bounding box
	private int width; // width of the text
	private int height; // height of the text

	// Array list of attributes which have all the tesseract Objects
	private ArrayList<TesseractAttributes> tessAtts = new ArrayList<>();

	/**
	 * The text and type of the scanner attribute as well as the upper left x
	 * and y values of the bounding box and the width and height of this
	 * bounding box
	 * 
	 * @param attributeText
	 *            text of the attribute
	 * @param attributeType
	 *            catogery of the attribute
	 * @param x
	 *            upper left x value of the bounding box
	 * @param y
	 *            upper left y value of the bounding box
	 * @param width
	 *            width of the text
	 * @param height
	 *            height of the text
	 */
	public ScannerAttributes(String attributeText, String attributeType,
			String x, String y, String width, String height) {
		this.attributeText = attributeText;
		this.attributeTyp = attributeType;
		try {
			this.x = Integer.parseInt(x);
			this.y = Integer.parseInt(y);

			this.width = Integer.parseInt(width);
			this.height = Integer.parseInt(height);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * add a Tesseract Attribute to the Array List which have roughly the same
	 * bounding boxes
	 * 
	 * @param tessBox
	 *            a new TesseractAttributes Object
	 */
	public void addTesseractBox(TesseractAttributes tessBox) {
		int pixelOffset = 5;
		if (x - pixelOffset <= tessBox.getX()
				&& x + width + pixelOffset >= tessBox.getX()
						+ tessBox.getWidth()) {
			if (y - pixelOffset <= tessBox.getY()
					&& y + height + pixelOffset >= tessBox.getY()
							+ tessBox.getHeight()) {
				tessAtts.add(tessBox);
			}

		}
	}

	// ----------------- Getters -------------------------
	public String getAttributeText() {
		return attributeText;
	}

	public String getAttributeTyp() {
		return attributeTyp;
	}

	public ArrayList<TesseractAttributes> getTessAtts() {
		return tessAtts;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
