package testing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class PictureDisplayTest {
	private final BufferedImage image;
	private Graphics g;

	public PictureDisplayTest(File filename) throws IOException {
		image = ImageIO.read(filename);
		g = image.getGraphics();
	}

	public void addText(Color c, int fontSize, Rectangle r, String text) {
		g.setFont(new Font("Courier", Font.PLAIN, fontSize));
		g.setColor(c);
		g.drawString(text, r.x, r.y);
		g.drawLine(r.x, r.y, r.x, r.y + r.height); //vert line left
		g.drawLine(r.x, r.y, r.x + r.width, r.y); //horizontal line top
		g.drawLine(r.x, r.y + r.height, r.x + r.width, r.y + r.height); //horizontal lower line
		g.drawLine(r.x + r.width, r.y, r.x + r.width, r.y + r.height); //vertical right line
	}

	public void finish(String filePath) throws IOException {
		g.dispose();
		ImageIO.write(image, "png", new File(filePath));
	}

}
