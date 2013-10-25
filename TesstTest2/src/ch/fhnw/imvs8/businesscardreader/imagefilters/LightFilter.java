package ch.fhnw.imvs8.businesscardreader.imagefilters;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Expects an 8 Bit image as input.
 * 
 * Expects the image to have a linear light difference in it. It takes the light
 * intensity of the four corners and interpolates a correction factor
 * 
 * @author Jon
 */
public class LightFilter implements ImageFilter {

	public LightFilter() {

	}

	@Override
	public ImagePlus filter(ImagePlus im) {
		int[] intensities = new int[4];
		ImageProcessor p = im.getProcessor();

		throw new NotImplementedException();
		// intensities[0] = im.getPixel(0, 0);

	}

}
