package ch.fhnw.imvs8.businesscardreader.imagefilters;

import ij.ImagePlus;
import ij.process.ImageProcessor;

public class CloseFilter implements ImageFilter {

	public CloseFilter() {
	}

	@Override
	public ImagePlus filter(ImagePlus im) {

		return dilate(erode(im));
	}

	private ImagePlus dilate(ImagePlus image) {
		int width = image.getWidth();
		int height = image.getHeight();
		ImageProcessor p = image.getProcessor();
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				if (p.getPixel(i, j) == 0) {
					if (i > 0 && p.getPixel(i - 1, j) == 255)
						p.putPixel(i - 1, j, 125);
					if (j > 0 && p.getPixel(i, j - 1) == 255)
						p.putPixel(i, j - 1, 125);
					if (i + 1 < height && p.getPixel(i + 1, j) == 255)
						p.putPixel(i + 1, j, 125);
					if (j + 1 < width && p.getPixel(i, j + 1) == 255)
						p.putPixel(i, j + 1, 125);
				}
			}
		}
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (p.getPixel(j, i) == 125) {
					p.putPixel(j, i, 0);
				}
			}
		}
		return image;
	}

	private ImagePlus dilateK(ImagePlus image, int k) {
		int width = image.getWidth();
		int height = image.getHeight();
		ImageProcessor p = manhattan(image.getProcessor(), width, height);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				p.putPixel(i, j, p.getPixel(i, j) <= k ? 255 : 0);
			}
		}
		return image;
	}

	// O(n^2) solution to find the Manhattan distance to "on" pixels in a two dimension array
	private ImageProcessor manhattan(ImageProcessor p, int width, int height) {
		// traverse from top left to bottom right
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (p.getPixel(i, j) == 0) {
					// first pass and pixel was on, it gets a zero
					p.putPixel(i, j, 255);
				} else {
					// pixel was off
					// It is at most the sum of the lengths of the array
					// away from a pixel that is on
					p.putPixel(i, j, width + height);
					// or one more than the pixel to the north
					if (i > 0)
						p.putPixel(i, j, Math.min(p.getPixel(i, j), p.getPixel(i - 1, j) + 1));
					// or one more than the pixel to the west
					if (j > 0)
						p.putPixel(i, j, Math.min(p.getPixel(i, j), p.getPixel(i, j - 1) + 1));
				}
			}
		}
		// traverse from bottom right to top left
		for (int i = height - 1; i >= 0; i--) {
			for (int j = width - 1; j >= 0; j--) {
				// either what we had on the first pass
				// or one more than the pixel to the south
				if (i + 1 < height)
					p.putPixel(i, j, Math.min(p.getPixel(i, j), p.getPixel(i + 1, j) + 1));
				// or one more than the pixel to the east
				if (j + 1 < width)
					p.putPixel(i, j, Math.min(p.getPixel(i, j), p.getPixel(i, j + 1) + 1));
			}
		}
		return p;
	}

	private ImagePlus erode(ImagePlus image) {
		int width = image.getWidth();
		int height = image.getHeight();
		ImageProcessor p = image.getProcessor();
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				if (p.getPixel(i, j) == 255) {
					if (i > 0 && p.getPixel(i - 1, j) == 0)
						p.putPixel(i - 1, j, 125);
					if (j > 0 && p.getPixel(i, j - 1) == 0)
						p.putPixel(i, j - 1, 125);
					if (i + 1 < height && p.getPixel(i + 1, j) == 0)
						p.putPixel(i + 1, j, 125);
					if (j + 1 < width && p.getPixel(i, j + 1) == 0)
						p.putPixel(i, j + 1, 125);
				}
			}
		}
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (p.getPixel(j, i) == 125) {
					p.putPixel(j, i, 255);
				}
			}
		}
		return image;
	}
}
