package ch.fhnw.imvs8.businesscardreader.imagefilters;

import java.awt.image.BufferedImage;

/**
 * Represents a filter bundle. The implementing class will be responsible for applying the filters
 * @author Jon
 *
 */
public interface FilterBundle {
	/**
	 * 
	 * @param im
	 * @return
	 */
	public BufferedImage applyFilters(BufferedImage im);
}
