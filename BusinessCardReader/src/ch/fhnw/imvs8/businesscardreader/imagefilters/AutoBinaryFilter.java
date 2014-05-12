package ch.fhnw.imvs8.businesscardreader.imagefilters;

import ij.ImagePlus;
import ij.process.AutoThresholder;
import ij.process.AutoThresholder.Method;
import ij.process.ImageProcessor;

/**
 * Converts an 8bit Grayscale image to binary image with a specific
 * Auto-Threshold strategy. It uses the Auto-Threshold strategy of the imageJ
 * API
 * 
 * @author Jon
 * 
 */
public class AutoBinaryFilter implements ImageFilter {
	Method m;

	/**
	 * uses the MaxEntropy strategy
	 */
	public AutoBinaryFilter() {
		this(Method.MaxEntropy);
	}

	public AutoBinaryFilter(Method m) {
		this.m = m;
	}

	@Override
	public ImagePlus filter(ImagePlus im) {
		ImageProcessor p = im.getProcessor();

		AutoThresholder thresholder = new AutoThresholder();
		int threshold = thresholder.getThreshold(m, p.getHistogram());
		p.threshold(threshold);

		return im;
	}

	@Override
	public String toString() {
		return "Binary filter with method: " + m.toString();
	}
}
