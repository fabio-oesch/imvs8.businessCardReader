package ch.fhnw.imvs8.businesscardreader.imagefilters;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.plugin.ContrastEnhancer;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;

import javax.imageio.ImageIO;

public class LaplaceSharpenFilter implements ImageFilter {
	//private final float[][] laplace = { { 0, 0, -1, 0, 0 }, { 0, -1, -2, -1, 0 }, { -1, -2, 16, -2, -1 }, { 0, -1, -2, -1, 0 }, { 0, 0, -1, 0, 0 } }; //laplace filter matrix
	//private final float[][] laplace = { { 0, 1, 0 }, { 1, -4, 1 }, { 0, 1, 0 } };
	private final float[][] laplace = { { -1, -1, -1 }, { -1, 8, -1 }, { -1, -1, -1 } };
	private final int size = 3;

	public LaplaceSharpenFilter() {
	}

	private ImagePlus convolve(ImagePlus original) {
		BufferedImage filtered = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB);
		ImagePlus filteredImage = new ImagePlus("filtered", filtered);
		ImageConverter ic = new ImageConverter(filteredImage);
		ic.convertToGray32();

		ImageProcessor ipf = filteredImage.getProcessor();
		ImageProcessor ipo = original.getProcessor();

		double minF = 1000000;
		double maxF = 0;
		double minO = 10000000;
		double maxO = 0;
		int size2 = size * size;
		for (int j = 0; j < original.getHeight(); j++) {
			for (int i = 0; i < original.getWidth(); i++) {

				//laplace filter is symetrical, we don't need to flip the filter matrix
				int sum = 0;
				int half = size / 2;
				for (int k = -half; k <= half; k++) {
					for (int g = -half; g <= half; g++) {
						int x = i + g;
						int y = j + k;
						if (x >= 0 && x < original.getWidth() && y >= 0 && y < original.getHeight()) {
							int val = ipo.getPixel(x, y);
							sum += ipo.getPixel(x, y) * laplace[g + half][k + half];
						} else {
							sum += ipo.getPixel(i, j) * laplace[g + half][k + half];
						}
					}
				}

				ipf.putPixelValue(i, j, sum);
				double val = ipf.getPixelValue(i, j);

				if (val > maxF)
					maxF = val;
				if (val < minF)
					minF = val;

			}
		}

		//convert back to grayscale 0-255
		maxF -= minF;
		for (int j = 0; j < original.getHeight(); j++) {
			for (int i = 0; i < original.getWidth(); i++) {
				double val = ipf.getPixelValue(i, j);
				val -= minF;
				val = val * (255.0 / maxF);
				ipf.putPixelValue(i, j, val);
			}
		}

		return new ImagePlus("filtered image", ipf);
	}

	@Override
	public ImagePlus filter(ImagePlus im) {
		ImageProcessor ipf = convolve(im).getProcessor();
		ImageProcessor ipo = im.getProcessor();

		double localMin = 10000000.0;
		double localMax = 0;
		for (int i = 0; i < ipo.getWidth(); i++) {
			for (int j = 0; j < ipo.getHeight(); j++) {
				double val = ipf.getPixelValue(i, j);
				val = ipo.getPixelValue(i, j) + val;
				ipf.putPixelValue(i, j, val);

				//delete
				if (val < localMin)
					localMin = val;
				if (val > localMax)
					localMax = val;
			}
		}

		//maybe not needed
		System.out.println(localMax);
		System.out.println(localMin);

		localMax -= localMin;
		for (int i = 0; i < ipo.getWidth(); i++) {
			for (int j = 0; j < ipo.getHeight(); j++) {
				double val = ipf.getPixelValue(i, j);
				val -= localMin;
				val = val * (255.0 / localMax);
				//ipf.putPixelValue(i, j, val);
			}
		}

		localMin = 1000000;
		localMax = 0;
		for (int i = 0; i < ipo.getWidth(); i++) {
			for (int j = 0; j < ipo.getHeight(); j++) {
				double val = ipf.getPixelValue(i, j);
				if (val < localMin)
					localMin = val;

				if (val > localMax)
					localMax = val;
			}
		}

		System.out.println(localMax);
		System.out.println(localMin);

		ContrastEnhancer enhancer = new ContrastEnhancer();

		//not a grayscale image anymore, convert to gray

		ImagePlus out = new ImagePlus("laplace filtered image", ipf);
		ImageConverter c = new ImageConverter(out);
		c.convertToGray8();

		return out;
	}

	public static void main(String[] args) throws Exception {
		BufferedImage image = ImageIO.read(new FileInputStream("sharpen.png"));
		ImagePlus im = new ImagePlus("", image);

		GrayScaleFilter g = new GrayScaleFilter();
		LaplaceSharpenFilter lp = new LaplaceSharpenFilter();
		im = g.filter(im);
		im = lp.filter(im);

		FileSaver s = new FileSaver(im);
		s.saveAsBmp("edge.bmp");
	}
}
