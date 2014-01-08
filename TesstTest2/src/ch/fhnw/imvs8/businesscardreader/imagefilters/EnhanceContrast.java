package ch.fhnw.imvs8.businesscardreader.imagefilters;

import ij.ImagePlus;
import ij.process.ImageProcessor;

/**
 * why you no enhance?
 * 
 * @author olry
 * 
 */
public class EnhanceContrast implements ImageFilter {
	int saturation = 30;
	private final double percentage = 1.50;

	/**
	 * Enhances the Contrast of the Picture
	 */
	public EnhanceContrast() {
	}

	@Override
	public ImagePlus filter(ImagePlus im) {

		//		ContrastEnhancer ce = new ContrastEnhancer();
		//		ce.stretchHistogram(im, saturation);
		//		return im;

		ImageProcessor p = im.getProcessor();
		for (int i = 0; i < im.getWidth(); i++) {
			for (int j = 0; j < im.getHeight(); j++) {
				int tmp = p.getPixel(i, j);
				p.putPixel(i, j, (int) (tmp * percentage));
			}
		}
		return new ImagePlus("Contrast enhanced", p);
	}

	@Override
	public String toString() {
		return "Enhance Contrast with saturation factor " + saturation;
	}

}
