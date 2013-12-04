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

	private double scaleX; // ratio of x-Axis
	private double scaleY; // ratio of y-Axis
	private double offsetX; // offset of the picture of x-Axis
	private double offsetY; // offset of the picture of y-Axis

	private int pixelOffset = 0;
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
	public ScannerAttributes(String attributeText, String attributeType, String x, String y, String width, String height) {
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
	public boolean addTesseractBox(TesseractAttributes tessBox) {
		double tesseractLeft = tessBox.getX() * scaleX - offsetX;
		double tesseractRight = (tessBox.getX() + tessBox.getWidth()) * scaleX - offsetX;
		double tesseractTop = tessBox.getY() * scaleY - offsetY;
		double tesseractBottom = (tessBox.getY() + tessBox.getHeight()) * scaleY - offsetY;
		if (x - pixelOffset <= tesseractLeft && x + width + pixelOffset >= tesseractRight) {
			if (y - pixelOffset <= tesseractTop && y + height + pixelOffset >= tesseractBottom) {
				tessAtts.add(tessBox);
				return true;
			}

		}
		return false;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		ScannerAttributes scanAtt = (ScannerAttributes) o;
		if (scanAtt.attributeText.equals(attributeText)) {
			return true;
		}
		return false;
	}

	// ----------------- Setters -------------------------
	public void setTesseractCorrection(double scaleX, double scaleY, double offsetX, double offsetY) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.offsetX = offsetX;
		this.offsetY = offsetY;

		//adaptive pixel tolerance
		double f = 1.0 - scaleX;
		f = f < 0 ? 0 : f;
		this.pixelOffset = (int) (f * 25) + 5;
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
