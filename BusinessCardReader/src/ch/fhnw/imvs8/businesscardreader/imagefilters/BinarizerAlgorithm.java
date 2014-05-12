package ch.fhnw.imvs8.businesscardreader.imagefilters;

import ij.ImagePlus;
import ij.gui.NewImage;
import ij.process.Blitter;
import ij.process.ImageProcessor;

/**
 * Represents the abstract base class of all BinarizationAlgorithms. It contains
 * methods used in all algorithms and implements the ImageFilter Inteface, so
 * the OCREngine class can use it properly.
 * 
 * @author Jon
 * 
 */
public abstract class BinarizerAlgorithm implements ImageFilter {

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
}
