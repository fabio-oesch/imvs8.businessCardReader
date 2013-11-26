package ch.fhnw.imvs8.businesscardreader.imagefilters;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import junit.framework.Assert;

import org.junit.Test;

public class RGBFilterTest {
	private static String colorImg = "colors.png";

	/**
	 * The RGBFilter expects a colored image on light background. It should find
	 */
	@Test
	public void testColorSeparation() {

		try {
			BufferedImage image = ImageIO.read(new FileInputStream(new File(
					colorImg)));

			RGBFilterBundle bundle = new RGBFilterBundle();
			BufferedImage out = bundle.applyFilters(image);
			if (ImageIO.write(out, "png", new File("RGBFilterTest.png"))) {

			} else
				Assert.fail();
		} catch (FileNotFoundException e) {
			Assert.fail();

		} catch (IOException e) {
			Assert.fail();
			e.printStackTrace();
		} // load
	}

}
