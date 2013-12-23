package ch.fhnw.imvs8.businesscardreader.imagefilters;

import ij.ImagePlus;
import ij.gui.NewImage;
import ij.process.Blitter;
import ij.process.ImageProcessor;

public abstract class Binarizer implements ImageFilter {

	@Deprecated
	protected long time;

	/**
	 * Does exactly what the name implies. Small helper which is needed in
	 * different Binarizer algorithms.
	 * 
	 * @param iProcessor
	 * @return
	 */
	protected static final ImagePlus duplicateImage(ImageProcessor iProcessor) {
		int w = iProcessor.getWidth();
		int h = iProcessor.getHeight();
		ImagePlus iPlus = NewImage.createByteImage("Image", w, h, 1, NewImage.FILL_BLACK);
		ImageProcessor imageProcessor = iPlus.getProcessor();
		imageProcessor.copyBits(iProcessor, 0, 0, Blitter.COPY);
		return iPlus;
	}

	/**
	 * Only used for test purposes, should be removed
	 * 
	 * @return the time spent in
	 */
	@Deprecated
	public long getUsedTime() {
		return time;
	}

	/**
	 * Only used for test purposes, should be removed
	 */
	@Deprecated
	public void resetUsedTime() {
		time = 0;
	}
}
