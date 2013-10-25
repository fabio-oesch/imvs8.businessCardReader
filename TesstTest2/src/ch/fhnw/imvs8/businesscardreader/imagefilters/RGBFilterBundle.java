package ch.fhnw.imvs8.businesscardreader.imagefilters;

import ij.ImagePlus;

import java.awt.image.BufferedImage;

/**
 * Represents a filter bundle which processes the R,G and B channel seperately.
 * It converts them into binary images and then substracts each channel for the final binary image output.
 * @author Jon
 *
 */
public class RGBFilterBundle extends FilterBundle {

	@Override
	public BufferedImage applyFilters(BufferedImage im) {
		ImagePlus plus = new ImagePlus("filtered_image",im);
		
		return this.convert(plus);
	}

}
