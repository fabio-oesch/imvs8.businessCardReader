package ch.fhnw.imvs8.businesscardreader.imagefilters;

import ij.ImagePlus;
import ij.process.ImageProcessor;

/**
 * Filter which should remove a linear light gradient out of an 24 Bit RGB Image
 * 
 * it assumes that the corner pixels should have the same color and corrects the
 * image accordingly.
 * 
 * @author Jonas Schwammberger, Fabio Oesch
 */
public class LightFilter implements ImageFilter {

	int pixelCount = 20;

	public LightFilter() {

	}

	public LightFilter(int pixelCount) {
		this.pixelCount = pixelCount;
	}

	@Override
	public ImagePlus filter(ImagePlus im) {
		final int width = im.getWidth() - 1;
		final int height = im.getHeight() - 1;

		final double w = width;
		final double h = height;
		int[] intensities = new int[4];
		ImageProcessor p = im.getProcessor();

		for (int i = 0; i < pixelCount; i++) {
			for (int j = 0; j < pixelCount; j++) {
				intensities[0] = p.getPixel(i, j); // upper left
				intensities[1] = p.getPixel(width - i, j); // upper right
				intensities[2] = p.getPixel(i, height - j); // lower left
				intensities[3] = p.getPixel(width - i, height - j);
			}
		}

		for (int i = 0; i < intensities.length; i++) {
			intensities[i] /= pixelCount * pixelCount;
		}

		int max = findMax(intensities);
		for (int i = 0; i < intensities.length; i++)
			intensities[i] = max - intensities[i];

		for (int j = 0; j < h; j++) {
			for (int i = 0; i < w; i++) {
				double value = p.getPixelValue(i, j) + intensities[0] * ((w - i) / w * (h - j) / h) + intensities[1] * (i / w * (h - j) / h) + intensities[2]
						* ((w - i) / w * j / h) + intensities[3] * (i / w * j / h);

				p.putPixelValue(i, j, value);
			}
		}

		return im;
	}

	private int findMax(int[] intensities) {
		int max = 0;
		for (int i = 0; i < intensities.length; i++)
			if (max < intensities[i])
				max = intensities[i];

		return max;
	}

}
