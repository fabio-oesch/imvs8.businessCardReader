package ch.fhnw.imvs8.businesscardreader.imagefilters;

import ij.ImagePlus;
import ij.plugin.filter.RankFilters;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;

public class Sauvola extends Binarizer {

	@Override
	public ImagePlus filter(ImagePlus im) {
		long start = System.currentTimeMillis();
		// Sauvola recommends K_VALUE = 0.5 and R_VALUE = 128.
		// This is a modification of Niblack's thresholding method.
		// Sauvola J. and Pietaksinen M. (2000) "Adaptive Document Image Binarization"
		// Pattern Recognition, 33(2): 225-236
		// http://www.ee.oulu.fi/mvg/publications/show_pdf.php?ID=24
		// Ported to ImageJ plugin from E Celebi's fourier_0.8 routines
		// This version uses a circular local window, instead of a rectagular one

		ImagePlus Meanimp, Varimp;
		ImageProcessor ip = im.getProcessor(), ipMean, ipVar;
		int radius = 25;
		double k_value = 0.5;
		double r_value = 128;
		byte object;
		byte backg;

		object = (byte) 0xff;
		backg = (byte) 0;

		Meanimp = duplicateImage(ip);
		ImageConverter ic = new ImageConverter(Meanimp);
		ic.convertToGray32();

		ipMean = Meanimp.getProcessor();
		RankFilters rf = new RankFilters();
		rf.rank(ipMean, radius, rf.MEAN);// Mean
		// Meanimp.show();
		Varimp = duplicateImage(ip);
		ic = new ImageConverter(Varimp);
		ic.convertToGray32();
		ipVar = Varimp.getProcessor();
		rf.rank(ipVar, radius, rf.VARIANCE); // Variance
		// Varimp.show();
		byte[] pixels = (byte[]) ip.getPixels();
		float[] mean = (float[]) ipMean.getPixels();
		float[] var = (float[]) ipVar.getPixels();

		for (int i = 0; i < pixels.length; i++)
			pixels[i] = (pixels[i] & 0xff) > (int) (mean[i] * (1.0 + k_value * (Math.sqrt(var[i]) / r_value - 1.0))) ? object : backg;
		// imp.updateAndDraw();

		long end = System.currentTimeMillis();
		super.time += end - start;
		return im;
	}

}
