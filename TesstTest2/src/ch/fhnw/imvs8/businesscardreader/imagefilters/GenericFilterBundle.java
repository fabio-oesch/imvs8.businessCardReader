package ch.fhnw.imvs8.businesscardreader.imagefilters;

import ij.ImagePlus;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the most generic filter bundle. It contains a list of filters and calls them sequentially
 * @author Jon
 */
public class GenericFilterBundle implements FilterBundle {
	List<ImageFilter> filters;
	
	public GenericFilterBundle() {
		filters = new ArrayList<>(10);
	}
	
	/**
	 * @param filters to use
	 */
	public GenericFilterBundle(List<ImageFilter> filters) {
		this.filters = filters;
	}

	@Override
	public BufferedImage applyFilters(BufferedImage im) {
		ImagePlus plus = new ImagePlus("filtered_image",im);
		
		for(ImageFilter f :filters)
			plus = f.filter(plus);
		
		/*there is no way to 'nicely' convert an ImageJ ImagePlus object to a BufferedImage object.
		 I don't like it but it works.*/
		Image image = plus.getImage();
		BufferedImage buffered = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_BYTE_GRAY);
		buffered.getGraphics().drawImage(image, 0, 0 , null);
		return buffered;
	}
	
	/**
	 * Appends a filter to the current list of filters
	 * @param f
	 */
	public void appendFilter(ImageFilter f) {
		this.filters.add(f);
	}
}
