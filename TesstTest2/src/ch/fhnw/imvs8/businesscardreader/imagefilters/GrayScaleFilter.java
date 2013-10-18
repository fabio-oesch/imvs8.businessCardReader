package ch.fhnw.imvs8.businesscardreader.imagefilters;

import ij.ImagePlus;
import ij.process.ImageConverter;

public class GrayScaleFilter implements ImageFilter {

	@Override
	public ImagePlus filter(ImagePlus im) {
		ImageConverter c = new ImageConverter(im);
		c.convertToGray8();
		return im;
	}
	
	@Override
	public String toString() {
		return "Grayscale filter";
	}

}
