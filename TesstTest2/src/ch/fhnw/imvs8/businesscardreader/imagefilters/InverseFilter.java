package ch.fhnw.imvs8.businesscardreader.imagefilters;

import ij.ImagePlus;
import ij.process.ImageProcessor;

public class InverseFilter implements ImageFilter {

	@Override
	public ImagePlus filter(ImagePlus im) {
		ImageProcessor p = im.getProcessor();
		int[] lut = new int[256];

		for (int i = 0; i < lut.length; i++)
			lut[i] = 255 - i;
		p.applyTable(lut);
		return im;
	}
}
