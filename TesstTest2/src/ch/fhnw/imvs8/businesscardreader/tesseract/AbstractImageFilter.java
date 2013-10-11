package ch.fhnw.imvs8.businesscardreader.tesseract;

import java.awt.image.BufferedImage;

/**
 * Represents an image filter. It takes image data and filters it for better Tesseract results.
 * @author Jon
 */
public abstract class AbstractImageFilter {
	protected AbstractImageFilter innerFilter;
	
	public AbstractImageFilter(AbstractImageFilter innerFilter) {
		this.innerFilter = innerFilter;
	}
	
	public abstract void filter(BufferedImage im);
	
	
}
