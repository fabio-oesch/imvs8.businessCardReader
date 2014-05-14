package ch.fhnw.imvs8.businesscardreader.imagefilters;

import ij.ImagePlus;
import ij.plugin.filter.RankFilters;
import ij.process.ImageProcessor;

public class Bernsen extends BinarizerAlgorithm {

	@Override
	public ImagePlus filter(ImagePlus imp) {
		// Bernsen recommends WIN_SIZE = 31 and CONTRAST_THRESHOLD = 15.
		// 1) Bernsen J. (1986) "Dynamic Thresholding of Grey-Level Images"
		// Proc. of the 8th Int. Conf. on Pattern Recognition, pp. 1251-1255
		// 2) Sezgin M. and Sankur B. (2004) "Survey over Image Thresholding
		// Techniques and Quantitative Performance Evaluation" Journal of
		// Electronic Imaging, 13(1): 146-165
		// http://citeseer.ist.psu.edu/sezgin04survey.html
		// Ported to ImageJ plugin from E Celebi's fourier_0.8 routines
		// This version uses a circular local window, instead of a rectagular
		// one
		ImagePlus Maximp, Minimp;
		ImageProcessor ip = imp.getProcessor(), ipMax, ipMin;
		int contrast_threshold = 15;
		int local_contrast;
		int mid_gray;
		int temp;
		int radius = 1; // change this value to change windowsize

		byte object = (byte) 0xff;
		byte backg = (byte) 0;

		Maximp = duplicateImage(ip);
		ipMax = Maximp.getProcessor();
		RankFilters rf = new RankFilters();
		rf.rank(ipMax, radius, RankFilters.MAX);// Maximum
		// Maximp.show();
		Minimp = duplicateImage(ip);
		ipMin = Minimp.getProcessor();
		rf.rank(ipMin, radius, RankFilters.MIN); // Minimum
		// Minimp.show();
		byte[] pixels = (byte[]) ip.getPixels();
		byte[] max = (byte[]) ipMax.getPixels();
		byte[] min = (byte[]) ipMin.getPixels();

		for (int i = 0; i < pixels.length; i++) {
			local_contrast = (max[i] & 0xff) - (min[i] & 0xff);
			mid_gray = ((min[i] & 0xff) + (max[i] & 0xff)) / 2;
			temp = pixels[i] & 0x0000ff;
			if (local_contrast < contrast_threshold)
				pixels[i] = mid_gray >= 128 ? object : backg; // Low contrast
																// region
			else
				pixels[i] = temp >= mid_gray ? object : backg;
		}
		// imp.updateAndDraw();
		return imp;
	}

	@Override
	public String toString() {
		return "Bernsen";
	}

}
