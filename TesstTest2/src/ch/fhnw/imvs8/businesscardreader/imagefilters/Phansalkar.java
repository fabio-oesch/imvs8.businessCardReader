package ch.fhnw.imvs8.businesscardreader.imagefilters;

import ij.ImagePlus;
import ij.plugin.ContrastEnhancer;
import ij.plugin.filter.RankFilters;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;

public class Phansalkar extends BinarizerAlgorithm {

	@Override
	public ImagePlus filter(ImagePlus im) {
		// This is a modification of Sauvola's thresholding method to deal with low contrast images.
		// Phansalskar N. et al. Adaptive local thresholding for detection of nuclei in diversity stained
		// cytology images.International Conference on Communications and Signal Processing (ICCSP), 2011,
		// 218 - 220.
		// In this method, the threshold t = mean*(1+p*exp(-q*mean)+k*((stdev/r)-1))
		// Phansalkar recommends k = 0.25, r = 0.5, p = 2 and q = 10. In this plugin, k and r are the
		// parameters 1 and 2 respectively, but the values of p and q are fixed.
		//
		// Implemented from Phansalkar's paper description by G. Landini
		// This version uses a circular local window, instead of a rectagular one

		ImagePlus Meanimp, Varimp, Orimp;
		ImageProcessor ip = im.getProcessor(), ipMean, ipVar, ipOri;
		double k_value = 0.25;
		double r_value = 0.5;
		double p_value = 2.0;
		double q_value = 10.0;
		int radius = 15;
		byte object;
		byte backg;

		object = (byte) 0xff;
		backg = (byte) 0;

		Meanimp = duplicateImage(ip);
		ContrastEnhancer ce = new ContrastEnhancer();
		ce.stretchHistogram(Meanimp, 0.0);
		ImageConverter ic = new ImageConverter(Meanimp);
		ic.convertToGray32();
		ipMean = Meanimp.getProcessor();
		ipMean.multiply(1.0 / 255);

		Orimp = duplicateImage(ip);
		ce.stretchHistogram(Orimp, 0.0);
		ic = new ImageConverter(Orimp);
		ic.convertToGray32();
		ipOri = Orimp.getProcessor();
		ipOri.multiply(1.0 / 255); // original to compare
		// Orimp.show();

		RankFilters rf = new RankFilters();
		rf.rank(ipMean, radius, rf.MEAN);// Mean

		// Meanimp.show();
		Varimp = duplicateImage(ip);
		ce.stretchHistogram(Varimp, 0.0);
		ic = new ImageConverter(Varimp);
		ic.convertToGray32();
		ipVar = Varimp.getProcessor();
		ipVar.multiply(1.0 / 255);

		rf.rank(ipVar, radius, rf.VARIANCE); // Variance
		ipVar.sqr(); // SD

		// Varimp.show();
		byte[] pixels = (byte[]) ip.getPixels();
		float[] ori = (float[]) ipOri.getPixels();
		float[] mean = (float[]) ipMean.getPixels();
		float[] sd = (float[]) ipVar.getPixels();

		for (int i = 0; i < pixels.length; i++)
			pixels[i] = ori[i] > mean[i] * (1.0 + p_value * Math.exp(-q_value * mean[i]) + k_value * (sd[i] / r_value - 1.0)) ? object : backg;
		// imp.updateAndDraw();

		return im;
	}

}
