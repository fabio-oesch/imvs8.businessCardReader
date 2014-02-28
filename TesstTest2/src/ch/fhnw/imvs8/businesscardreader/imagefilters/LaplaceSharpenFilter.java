package ch.fhnw.imvs8.businesscardreader.imagefilters;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.ImageProcessor;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;

import javax.imageio.ImageIO;

public class LaplaceSharpenFilter implements ImageFilter {
	private float[][] laplace = { { 0, 0, 1, 0, 0 }, { 0, 1, 2, 1, 0 }, { 1, 2, -16, 2, 1 }, { 0, 1, 2, 1, 0 }, { 0, 0, 1, 0, 0 } }; //laplace filter matrix
	private int size = 5;

	private double weight = 1.0;

	public LaplaceSharpenFilter() {

	}

	private ImagePlus convolve(ImagePlus original) {
		BufferedImage filtered = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_USHORT_GRAY);
		ImagePlus filteredImage = new ImagePlus("filtered", filtered);
		ImageProcessor ipf = filteredImage.getProcessor();
		ImageProcessor ipo = original.getProcessor();

		int size2 = size * size;
		for (int i = 0; i < original.getWidth(); i++) {
			for (int j = 0; j < original.getHeight(); j++) {
				//laplace filter is symetrical, we don't need to flip the filter matrix
				double sum = 0;
				int half = size / 2;
				for (int k = -half; k <= half; k++) {
					for (int g = -half; g <= half; g++) {
						int x = i + g;
						int y = j + k;
						if (x >= 0 && x < original.getWidth() && y >= 0 && y < original.getHeight()) {
							sum += ipo.getPixelValue(x, y) * laplace[k + half][g + half];
						}
					}
				}

				ipf.putPixelValue(i, j, sum);
			}
		}

		return new ImagePlus("filtered image", ipf);
	}

	@Override
	public ImagePlus filter(ImagePlus im) {
		ImageProcessor ipf = convolve(im).getProcessor();
		ImageProcessor ipo = im.getProcessor();

		for (int i = 0; i < ipo.getWidth(); i++) {
			for (int j = 0; j < ipo.getHeight(); j++) {
				double val = this.weight * ipf.getPixelValue(i, j);
				ipf.putPixelValue(i, j, ipo.getPixelValue(i, j) - val);
			}
		}
		return new ImagePlus("laplace filtered image", ipf);
	}

	public static void main(String[] args) throws Exception {

		BufferedImage image = ImageIO.read(new FileInputStream("table_edge.jpg"));
		ImagePlus im = new ImagePlus("", image);
		GrayScaleFilter g = new GrayScaleFilter();
		LaplaceSharpenFilter lp = new LaplaceSharpenFilter();
		im = g.filter(im);
		im = lp.filter(im);
		FileSaver s = new FileSaver(im);
		s.saveAsBmp("edge.bmp");
	}
}
