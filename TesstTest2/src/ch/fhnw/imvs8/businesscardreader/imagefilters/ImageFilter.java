package ch.fhnw.imvs8.businesscardreader.imagefilters;

import ij.ImagePlus;

/**
 * Represents an Image filter which uses the ImageJ library
 * 
 * @author Jon
 */
public interface ImageFilter {

	/**
	 * @param im
	 *            image to filter
	 * @return filtered image
	 */
	public ImagePlus filter(ImagePlus im);
}
