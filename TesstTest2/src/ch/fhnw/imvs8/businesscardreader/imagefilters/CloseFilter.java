package ch.fhnw.imvs8.businesscardreader.imagefilters;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.ImageProcessor;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;

import javax.imageio.ImageIO;

public class CloseFilter implements ImageFilter {
	private int fgPixel; //foreground pixel value
	private int bgPixel;

	public CloseFilter() {
		bgPixel = 255;
		fgPixel = 0;
	}

	@Override
	public ImagePlus filter(ImagePlus im) {
		ImageProcessor p = im.getProcessor();
		dilate(p, fgPixel, bgPixel);
		dilate(p, bgPixel, fgPixel); //erode by swapping foreground to background
		return im;
	}

	private void dilate(ImageProcessor p, int foreground, int background) {
		int width = p.getWidth();
		int height = p.getHeight();
		int tmp = 125; //mark pixel as temporary, meaning will become a foreground pixel afterwards
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				System.out.println(p.getPixel(i, j));
				if (p.getPixel(i, j) == background) {
					if (i > 0 && p.getPixel(i - 1, j) == foreground)
						p.putPixel(i, j, tmp);
					if (j > 0 && p.getPixel(i, j - 1) == foreground)
						p.putPixel(i, j, tmp);
					if (i + 1 < height && p.getPixel(i + 1, j) == foreground)
						p.putPixel(i, j, tmp);
					if (j + 1 < width && p.getPixel(i, j + 1) == foreground)
						p.putPixel(i, j, tmp);
				}
			}
		}
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (p.getPixel(j, i) == tmp) {
					p.putPixel(j, i, foreground);
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		BufferedImage image = ImageIO.read(new FileInputStream("close.png"));
		ImagePlus p = new ImagePlus("", image);
		GrayScaleFilter gray = new GrayScaleFilter();
		CloseFilter close = new CloseFilter();
		p = gray.filter(p);
		p = close.filter(p);
		FileSaver saver = new FileSaver(p);
		saver.saveAsBmp("closeOut.bmp");
	}
}
