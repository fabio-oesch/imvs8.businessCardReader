package testing;

public class DivAttribute {
	
	private String attributeText;


	private int x;
	private int y;
	private int width;
	private int height;

	public DivAttribute(String attributeText, String x, String y,
			String width, String height) {
		this.attributeText = attributeText;
		try {
			this.x = Integer.parseInt(x);
			this.y = Integer.parseInt(y);
			this.width = Integer.parseInt(width);
			this.height = Integer.parseInt(height);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public String getAttributeText() {
		return attributeText;
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
