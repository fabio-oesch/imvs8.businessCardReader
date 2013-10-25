package ch.fhnw.imvs8.businesscardreader.imagefilters;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import ij.ImagePlus;

/**
 * Expects an 8 Bit image as input.
 * 
 * Expects the image to have a linear light difference in it.
 * It takes the light intensity of the four corners and interpolates a smooth lighting
 * 
 * @author Jon
 */
public class LightFilter implements ImageFilter {

	public LightFilter() {
		
	}
	
	@Override
	public ImagePlus filter(ImagePlus im) {
		int[] intensities = new int[4];
		
		throw new NotImplementedException();
		//intensities[0] = im.getPixel(0, 0);
		
		
	}

}
