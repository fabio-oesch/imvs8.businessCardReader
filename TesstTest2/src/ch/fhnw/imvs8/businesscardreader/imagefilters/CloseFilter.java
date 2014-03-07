package ch.fhnw.imvs8.businesscardreader.imagefilters;

import ij.ImagePlus;
import ij.process.ImageProcessor;

public class CloseFilter implements ImageFilter {
	private int fgPixel; //foreground pixel value
	private int bgPixel;

	public CloseFilter() {
		bgPixel = 255;
		fgPixel = 0;
	}

	@Override
	public ImagePlus filter(ImagePlus im) {
		ImageProcessor p = im.getProcessor();
		dilate(p, fgPixel, bgPixel);
		dilate(p, bgPixel, fgPixel); //erode by swapping foreground to background
		return im;
	}

	private void dilate(ImageProcessor p, int foreground, int background) {
		int width = p.getWidth();
		int height = p.getHeight();
		int tmp = 125; //mark pixel as temporary, meaning will become a foreground pixel afterwards
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				if (p.getPixel(i, j) == background) {
					if (i > 0 && p.getPixel(i - 1, j) == foreground)
						p.putPixel(i, j, tmp);
					if (j > 0 && p.getPixel(i, j - 1) == foreground)
						p.putPixel(i, j, tmp);
					if (i + 1 < height && p.getPixel(i + 1, j) == foreground)
						p.putPixel(i, j, tmp);
					if (j + 1 < width && p.getPixel(i, j + 1) == foreground)
						p.putPixel(i, j, tmp);
				}
			}
		}
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (p.getPixel(j, i) == tmp) {
					p.putPixel(j, i, foreground);
				}
			}
		}
	}
}
