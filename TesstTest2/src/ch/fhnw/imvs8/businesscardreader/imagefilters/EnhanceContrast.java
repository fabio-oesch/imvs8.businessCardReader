package ch.fhnw.imvs8.businesscardreader.imagefilters;

import ij.ImagePlus;
import ij.plugin.ContrastEnhancer;

/**
 * why you no enhance?
 * 
 * @author olry
 * 
 */
public class EnhanceContrast extends BinarizerAlgorithm {
	int saturation = 3;

	/**
	 * Enhances the Contrast of the Picture
	 */
	public EnhanceContrast() {
	}

	@Override
	public ImagePlus filter(ImagePlus im) {
		ContrastEnhancer ce = new ContrastEnhancer();
		ce.stretchHistogram(im, saturation);
		return im;
	}

	@Override
	public String toString() {
		return "Enhance Contrast with saturation factor " + saturation;
	}

}
