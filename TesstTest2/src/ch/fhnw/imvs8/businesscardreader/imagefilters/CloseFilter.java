package ch.fhnw.imvs8.businesscardreader.imagefilters;

import ij.ImagePlus;
import ij.process.BinaryProcessor;
import ij.process.ByteProcessor;

public class CloseFilter implements ImageFilter {

	public CloseFilter() {
	}

	@Override
	public ImagePlus filter(ImagePlus im) {
		BinaryProcessor p = new BinaryProcessor(new ByteProcessor(im.getImage()));
		p.dilate();
		p.erode();
		return im;
	}
}
