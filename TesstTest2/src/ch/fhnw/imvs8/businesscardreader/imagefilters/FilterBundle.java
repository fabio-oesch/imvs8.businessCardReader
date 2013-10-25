package ch.fhnw.imvs8.businesscardreader.imagefilters;

import ij.ImagePlus;

import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * Represents a filter bundle. The implementing class will be responsible for applying the filters.
 * 
 * it encapsulates all Image Processing functions, so the IP-Library can be switched with encapsulated consequences.
 * @author Jon
 *
 */
public abstract class FilterBundle {
	
	/**
	 * @param im image to filter
	 * @return filtered image. Does not have to be the same object.
	 */
	public abstract BufferedImage applyFilters(BufferedImage im);
	
	/**
	 * there is no way to 'nicely' convert an ImageJ ImagePlus object to a BufferedImage object.
		 I don't like it but it works.
	 * @param im 
	 * @return converted image
	 */
	protected BufferedImage convert(ImagePlus im) {
		Image image = im.getImage();
		BufferedImage buffered = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_BYTE_GRAY);
		buffered.getGraphics().drawImage(image, 0, 0 , null);
		return buffered;
	}
	
}
