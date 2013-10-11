package testing;

import java.util.ArrayList;

public class ScannerAttributes {

	private String attributeText;
	private String attributeTyp;

	private int x;
	private int y;
	private int width;
	private int height;
	private ArrayList<TesseractAttributes> tessAtts = new ArrayList<>();

	public ScannerAttributes(String attributeText, String attributeTyp,
			String x, String y, String width, String height) {
		this.attributeText = attributeText;
		this.attributeTyp = attributeTyp;
		try {
			this.x = Integer.parseInt(x);
			this.y = Integer.parseInt(y);

			this.width = Integer.parseInt(width);
			this.height = Integer.parseInt(height);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void addTesseractBox(TesseractAttributes tessBox) {
		if (x - 5 <= tessBox.getX()
				&& x + width + 5 >= tessBox.getX() + tessBox.getWidth()) {
			if (y - 5 <= tessBox.getY()
					&& x + height + 5 >= tessBox.getY() + tessBox.getHeight()) {
				tessAtts.add(tessBox);
			}

		}
	}

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
