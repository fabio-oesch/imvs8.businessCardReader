package ch.fhnw.imvs8.businesscardreader.imagefilters;

import ij.ImagePlus;
import ij.process.ImageProcessor;

/**
 * 
 * @author Jon
 */
public class InverseFilter implements ImageFilter {

	int[] lut;

	public InverseFilter() {
		super();
		lut = new int[256];
		for (int i = 0; i < lut.length; i++)
			lut[i] = 255 - i;
	}

	@Override
	public ImagePlus filter(ImagePlus im) {
		ImageProcessor p = im.getProcessor();
		p.applyTable(lut);
		return im;
	}
}
