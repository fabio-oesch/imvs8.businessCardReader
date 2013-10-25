package testing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
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

	public void addText(Color c, int fontSize, int x, int y, String text) {
		g.setFont(new Font("Courier", Font.PLAIN, fontSize));
		g.setColor(c);
		g.drawString(text, x, y);
	}

	public void finish(String filePath) throws IOException {
		g.dispose();
		ImageIO.write(image, "png", new File(filePath));
	}

}
