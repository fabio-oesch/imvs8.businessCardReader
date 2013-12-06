package ch.fhnw.imvs8.businesscardreader.imagefilters;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class CloseFilter implements ImageFilter {

	public CloseFilter() {
	}

	@Override
	public ImagePlus filter(ImagePlus im) {
		ByteProcessor p = new ByteProcessor(im.getImage());
		// int[] filter = { 0, 0, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 1, 0, 0 };
		int[] filter = { 0, 1, 0, 1, 1, 1, 0, 1, 0 };
		// p.applyTable(filter);
		p.filter(ImageProcessor.MAX);
		p.filter(ImageProcessor.MIN);
		// p.dilate(100, 0);
		// p.erode();
		return im;
	}
}
