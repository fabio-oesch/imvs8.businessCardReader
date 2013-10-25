package ch.fhnw.imvs8.businesscardreader.imagefilters;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import ij.process.AutoThresholder.Method;

/**
 * Converts an 8bit Grayscale image to binary image with a specific Auto-Threshold strategy.
 * @author Jon
 *
 */
public class AutoBinaryFilter implements ImageFilter{
	Method m;
	
	/**
	 * uses threshold = 127
	 */
	public AutoBinaryFilter() { m = Method.MaxEntropy;}
	
	
	@Override
	public ImagePlus filter(ImagePlus im) {
		ImageProcessor p = im.getProcessor();
		
		p.setAutoThreshold(Method.MaxEntropy, false);
		p.autoThreshold();
		
		return im;
	}
	
	@Override
	public String toString() {
		return "Binary filter with method: "+m.toString();
	}
}
