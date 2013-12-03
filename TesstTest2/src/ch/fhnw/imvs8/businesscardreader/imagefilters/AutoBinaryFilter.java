package ch.fhnw.imvs8.businesscardreader.imagefilters;

import ij.ImagePlus;
import ij.gui.NewImage;
import ij.plugin.ContrastEnhancer;
import ij.plugin.filter.RankFilters;
import ij.process.AutoThresholder.Method;
import ij.process.Blitter;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;

/**
 * Converts an 8bit Grayscale image to binary image with a specific Auto-Threshold strategy. It uses the
 * Auto-Threshold strategy of the imageJ API
 * 
 * @author Jon
 * 
 */
public class AutoBinaryFilter implements ImageFilter {
	Method m;

	/**
	 * uses the MaxEntropy strategy
	 */
	public AutoBinaryFilter() {
		m = Method.MaxEntropy;
	}

	@Override
	public ImagePlus filter(ImagePlus im) {
		// ImageProcessor p = im.getProcessor();
		//
		// p.setAutoThreshold(Method.MaxEntropy, false);
		// p.autoThreshold();
		//
		// return im;
		return Phansalkar(im);
	}

	@Override
	public String toString() {
		return "Binary filter with method: " + m.toString();
	}

	public ImagePlus Phansalkar(ImagePlus imp) {
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
		ImageProcessor ip = imp.getProcessor(), ipMean, ipVar, ipOri;
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
			pixels[i] = ((ori[i]) > (mean[i] * (1.0 + p_value * Math.exp(-q_value * mean[i]) + k_value
					* ((sd[i] / r_value) - 1.0)))) ? object : backg;
		// imp.updateAndDraw();
		return imp;
	}

	ImagePlus Sauvola(ImagePlus imp) {
		// Sauvola recommends K_VALUE = 0.5 and R_VALUE = 128.
		// This is a modification of Niblack's thresholding method.
		// Sauvola J. and Pietaksinen M. (2000) "Adaptive Document Image Binarization"
		// Pattern Recognition, 33(2): 225-236
		// http://www.ee.oulu.fi/mvg/publications/show_pdf.php?ID=24
		// Ported to ImageJ plugin from E Celebi's fourier_0.8 routines
		// This version uses a circular local window, instead of a rectagular one

		ImagePlus Meanimp, Varimp;
		ImageProcessor ip = imp.getProcessor(), ipMean, ipVar;
		int radius = 200;
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
			pixels[i] = ((pixels[i] & 0xff) > (int) (mean[i] * (1.0 + k_value
					* ((Math.sqrt(var[i]) / r_value) - 1.0)))) ? object : backg;
		// imp.updateAndDraw();
		return imp;
	}

	private ImagePlus duplicateImage(ImageProcessor iProcessor) {
		int w = iProcessor.getWidth();
		int h = iProcessor.getHeight();
		ImagePlus iPlus = NewImage.createByteImage("Image", w, h, 1, NewImage.FILL_BLACK);
		ImageProcessor imageProcessor = iPlus.getProcessor();
		imageProcessor.copyBits(iProcessor, 0, 0, Blitter.COPY);
		return iPlus;
	}

}
