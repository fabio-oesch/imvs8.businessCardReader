package testing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class PictureDisplayTest {
	private final BufferedImage image2;
	private Graphics g;

	public PictureDisplayTest(File filename) throws IOException {
		image2 = ImageIO.read(filename);
		g = image2.getGraphics();
	}

	public void addText(Color c, int x, int y, String text) {
		g.setFont(g.getFont().deriveFont(30f));
		g.setColor(c);
		g.drawString(text, x, y);
	}

	public void finish(String filePath) throws IOException {
		g.dispose();
		ImageIO.write(image2, "png", new File(filePath));
	}

}
