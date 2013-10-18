package ch.fhnw.imvs8.businesscardreader.imagefilters;

import ij.ImagePlus;
import ij.process.ImageProcessor;

/**
 * Converts an 8bit Grayscale image to a 8bit binary. Min = 0, max =255;
 * @author Jon
 *
 */
public class BinaryFilter implements ImageFilter{
	int thresh = 127;
	
	/**
	 * uses threshold = 127
	 */
	public BinaryFilter() { }
	
	/**
	 * 
	 * @param threshold expected unsigned byte
	 */
	public BinaryFilter(byte threshold) {
		thresh = threshold & 0xff;	//convert unsigned byte to integer
	}
	
	@Override
	public ImagePlus filter(ImagePlus im) {
		ImageProcessor p = im.getProcessor();
		int[] hist = p.getHistogram();
		
		//uses histogram as lookup table. 
		for(int i = 0; i < hist.length;i++)
			hist[i] = i > thresh? 255:0;
		
		p.applyTable(hist);
		return im;
	}
	
	@Override
	public String toString() {
		return "Binary filter with threshold: "+this.thresh;
	}
}
