package testing;

/**
 * An attribute of the tesseract HTML.
 * 
 * @author O Lry
 * 
 */
public class TesseractAttributes {

	private String attributeText; // The text which is in at the position x and
									// y
	private int x; // upper left x value of the bounding box
	private int y; // upper left y value of the bounding box
	private int width; // width of the text
	private int height; // height of the text

	/**
	 * text of the attribute as well as the coordinates of the upper left corner
	 * and the lower right corner. This will transform the lower right corner
	 * into height and width
	 * 
	 * @param attributeText
	 *            text of the attribute
	 * @param xUpperLeft
	 *            upper left x value of the bounding box
	 * @param yUpperLeft
	 *            upper left y value of the bounding box
	 * @param xLowerRight
	 *            lower right x value of the bounding box
	 * @param yLowerRight
	 *            lower right y value of the bounding box
	 */
	public TesseractAttributes(String attributeText, String xUpperLeft,
			String yUpperLeft, String xLowerRight, String yLowerRight) {
		this.attributeText = attributeText;
		try {
			x = Integer.parseInt(xUpperLeft);
			y = Integer.parseInt(yUpperLeft);

			width = Integer.parseInt(xLowerRight) - x;
			height = Integer.parseInt(yLowerRight) - y;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// ----------------- Getters -------------------------
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

	public String getAttributeText() {
		return attributeText;
	}

}
