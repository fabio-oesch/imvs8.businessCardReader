package ch.fhnw.imvs8.businesscardreader.imagefilters;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ColorProcessor;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a filter bundle which processes the R,G and B channel seperately.
 * It converts them into binary images and then substracts each channel for the
 * final binary image output.
 * 
 * @author Jon
 * 
 */
public class RGBFilterBundle extends FilterBundle {
	private List<ImageFilter> filters;

	public RGBFilterBundle() {
		// TODO: add LightFilter
		filters = new ArrayList<>(2);
		// filters.add(new LightFilter());
		filters.add(new AutoBinaryFilter());
	}

	@Override
	public BufferedImage applyFilters(BufferedImage im) {
		ImagePlus plus = new ImagePlus("filtered_image", im);
		System.out.println(ImagePlus.COLOR_RGB);
		System.out.println(plus.getType());

		ImagePlus[] channels = this.splitRGB(plus);

		// filter
		for (int i = 0; i < channels.length; i++)
			for (ImageFilter f : filters)
				channels[i] = f.filter(channels[i]);

		System.out.println(channels.length);
		BufferedImage out = new BufferedImage(channels[0].getWidth(),
				channels[0].getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		// create output
		for (int i = 0; i < out.getWidth(); i++)
			for (int j = 0; j < out.getHeight(); j++) {

				// get minimum from channels
				int min = 255;
				for (int k = 0; k < channels.length; k++)
					if (min > (channels[k].getPixel(i, j))[k])
						min = (channels[k].getPixel(i, j))[k];

				out.setRGB(i, j, min);
			}

		return out;
	}

	/**
	 * refactored methods from ChannelSplitter plugin
	 * 
	 * @param im
	 * @return
	 */
	private ImagePlus[] splitRGB(ImagePlus im) {
		ImageStack[] channels = splitRGB(im.getStack(), false);

		// throw away image
		im.close();

		ImagePlus rImp = new ImagePlus("red", channels[0]);
		ImagePlus gImp = new ImagePlus("green", channels[1]);
		ImagePlus bImp = new ImagePlus("blue)", channels[2]);
		return new ImagePlus[] { rImp, gImp, bImp };
	}

	public static ImageStack[] splitRGB(ImageStack rgb, boolean keepSource) {
		int w = rgb.getWidth();
		int h = rgb.getHeight();
		ImageStack[] channels = new ImageStack[3];
		for (int i = 0; i < 3; i++)
			channels[i] = new ImageStack(w, h);
		byte[] r, g, b;
		ColorProcessor cp = (ColorProcessor) rgb.getProcessor(1);

		r = new byte[w * h];
		g = new byte[w * h];
		b = new byte[w * h];
		cp.getRGB(r, g, b);
		if (!keepSource)
			rgb.deleteSlice(1);
		channels[0].addSlice(null, r);
		channels[1].addSlice(null, g);
		channels[2].addSlice(null, b);

		return channels;
	}
}
