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

	public LightFilter() {

	}

	@Override
	public ImagePlus filter(ImagePlus im) {
		int[] intensities = new int[4];
		ImageProcessor p = im.getProcessor();

		intensities[0] = p.getPixel(0, 0); //upper left
		intensities[1] = p.getPixel(im.WIDTH - 1, 0); //upper right
		intensities[2] = p.getPixel(0, im.HEIGHT - 1); //lower left
		intensities[3] = p.getPixel(im.WIDTH - 1, im.HEIGHT - 1);

		int max = findMax(intensities);
		for (int i = 0; i < intensities.length; i++)
			intensities[i] -= max;

		double dY1 = (intensities[2] - intensities[0]) / (double) im.WIDTH;
		double dY2 = (intensities[3] - intensities[1]) / (double) im.WIDTH;

		double yCorr1 = intensities[0]; //correction value for left y axis
		double yCorr2 = intensities[0];
		; //correction value for right y axis

		for (int j = 0; j < im.HEIGHT; j++) {
			double dX = (yCorr2 - yCorr1) / im.WIDTH;
			double xCorr = p.getPixel(0, j);

			for (int i = 0; i < im.WIDTH; i++) {
				int val = (int) (p.getPixel(i, j) + xCorr);
				p.putPixel(i, j, val);

				xCorr += dX;
			}

			yCorr1 += dY1;
			yCorr2 += dY2;
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
