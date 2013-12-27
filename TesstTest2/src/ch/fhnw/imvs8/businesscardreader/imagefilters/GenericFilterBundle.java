package ch.fhnw.imvs8.businesscardreader.imagefilters;

import ij.ImagePlus;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the most generic filter bundle. It contains a list of filters and
 * applies them sequentially
 * 
 * @author Jon
 */
public class GenericFilterBundle implements FilterBundle {
	List<ImageFilter> filters;
	private long time;
	private int pictureCount;

	public GenericFilterBundle() {
		this(new ArrayList<ImageFilter>(10));
	}

	/**
	 * @param filters
	 *            to use
	 */
	public GenericFilterBundle(List<ImageFilter> filters) {
		this.filters = filters;
		this.time = 0;
		this.pictureCount = 0;
	}

	@Override
	public BufferedImage applyFilters(BufferedImage im) {
		this.pictureCount++;
		ImagePlus plus = new ImagePlus("filtered_image", im);
		long start = System.currentTimeMillis();

		for (ImageFilter f : filters)
			plus = f.filter(plus);

		long end = System.currentTimeMillis();
		this.time += end - start;

		return plus.getBufferedImage();
	}

	/**
	 * Appends a filter to the current list of filters
	 * 
	 * @param f
	 */
	public void appendFilter(ImageFilter f) {
		this.filters.add(f);
	}

	/**
	 * @return the time the Filterbundle used to filter all images in
	 *         Milliseconds
	 */
	public long getUsedTimeMilis() {
		return time;
	}

	/**
	 * 
	 * @return number of pictures filtered by this object
	 */
	public int getFilteredPictureCount() {
		return pictureCount;
	}
}
