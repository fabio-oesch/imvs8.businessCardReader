package ch.fhnw.imvs8.businesscardreader.imagefilters;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.OvalRoi;
import ij.gui.Roi;
import ij.process.ImageProcessor;

public class Otsu extends BinarizerAlgorithm {

	@Override
	public ImagePlus filter(ImagePlus imp) {
		// Otsu's threshold algorithm
		// C++ code by Jordan Bevik <Jordan.Bevic@qtiworld.com>
		// ported to ImageJ plugin by G.Landini. Same algorithm as in
		// Auto_Threshold, this time on local circular regions
		int[] data;
		int radius = 1; // change radius for different outcomes
		int w = imp.getWidth();
		int h = imp.getHeight();
		int position;
		int radiusx2 = radius * 2;
		ImageProcessor ip = imp.getProcessor();
		byte[] pixels = (byte[]) ip.getPixels();
		byte[] pixelsOut = new byte[pixels.length]; // need this to avoid
													// changing the image data
													// (and further histograms)

		byte object = (byte) 0xff;
		byte backg = (byte) 0;

		int k, kStar; // k = the current threshold; kStar = optimal threshold
		int N1, N; // N1 = # points with intensity <=k; N = total number of
					// points
		double BCV, BCVmax; // The current Between Class Variance and maximum
							// BCV
		double num, denom; // temporary bookeeping
		int Sk; // The total intensity for all histogram points <=k
		int S, L = 256; // The total intensity of the image. Need to hange here
						// if modifying for >8 bits images
		int roiy;

		Roi roi = new OvalRoi(0, 0, radiusx2, radiusx2);
		// ip.setRoi(roi);
		for (int y = 0; y < h; y++) {
			IJ.showProgress((double) y / (h - 1)); // this method is slow, so
													// let's show the
													// progress bar
			roiy = y - radius;
			for (int x = 0; x < w; x++) {
				roi.setLocation(x - radius, roiy);
				ip.setRoi(roi);
				// ip.setRoi(new OvalRoi(x-radius, roiy, radiusx2, radiusx2));
				position = x + y * w;
				data = ip.getHistogram();

				// Initialize values:
				S = N = 0;
				for (k = 0; k < L; k++) {
					S += k * data[k]; // Total histogram intensity
					N += data[k]; // Total number of data points
				}

				Sk = 0;
				N1 = data[0]; // The entry for zero intensity
				BCV = 0;
				BCVmax = 0;
				kStar = 0;

				// Look at each possible threshold value,
				// calculate the between-class variance, and decide if it's a
				// max
				for (k = 1; k < L - 1; k++) { // No need to check endpoints k =
												// 0 or k = L-1
					Sk += k * data[k];
					N1 += data[k];

					// The float casting here is to avoid compiler warning about
					// loss of precision and
					// will prevent overflow in the case of large saturated
					// images
					denom = (double) N1 * (N - N1); // Maximum value of denom
													// is (N^2)/4 = approx.
													// 3E10

					if (denom != 0) {
						// Float here is to avoid loss of precision when
						// dividing
						num = (double) N1 / N * S - Sk; // Maximum value of
														// num = 255*N =
														// approx 8E7
						BCV = num * num / denom;
					} else
						BCV = 0;

					if (BCV >= BCVmax) { // Assign the best threshold found so
											// far
						BCVmax = BCV;
						kStar = k;
					}
				}
				// kStar += 1; // Use QTI convention that intensity -> 1 if
				// intensity >= k
				// (the algorithm was developed for I-> 1 if I <= k.)
				// return kStar;
				pixelsOut[position] = (pixels[position] & 0xff) > kStar ? object : backg;
			}
		}
		for (position = 0; position < w * h; position++)
			pixels[position] = pixelsOut[position]; // update with thresholded
													// pixels
		return imp;
	}

	@Override
	public String toString() {
		return "Otsu";
	}
}
