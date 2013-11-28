package ch.fhnw.imvs8.businesscardreader.imagefilters;

import java.awt.image.BufferedImage;

/**
 * Represents a filter bundle. The implementing class will be responsible for
 * applying the filters.
 * 
 * it encapsulates all Image Processing functions, so the IP-Library can be
 * switched with encapsulated consequences.
 * 
 * @author Jon
 * 
 */
public interface FilterBundle {

	/**
	 * @param im
	 *            image to filter
	 * @return filtered image. Does not have to be the same object.
	 */
	public abstract BufferedImage applyFilters(BufferedImage im);

}
