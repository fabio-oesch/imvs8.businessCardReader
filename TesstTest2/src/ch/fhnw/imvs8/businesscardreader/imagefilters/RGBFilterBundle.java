package ch.fhnw.imvs8.businesscardreader.imagefilters;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ColorProcessor;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a filter bundle which processes the RGB channels seperately.
 * 
 * 
 * @author Jon
 * 
 */
public class RGBFilterBundle implements FilterBundle {
	private List<ImageFilter> channelFilters;

	public RGBFilterBundle() {
		channelFilters = new ArrayList<>(2);
		// filters.add(new LightFilter());
		channelFilters.add(new AutoBinaryFilter());
		//filters.add(new InverseFilter());
	}

	@Override
	public BufferedImage applyFilters(BufferedImage im) {
		ImagePlus plus = new ImagePlus("filtered_image", im);

		//filter image

		ImagePlus[] channels = this.splitRGB(plus);
		// filter channels
		for (int i = 0; i < channels.length; i++)
			for (ImageFilter f : channelFilters)
				channels[i] = f.filter(channels[i]);

		// create new output image
		BufferedImage out = new BufferedImage(channels[0].getWidth(), channels[0].getHeight(), BufferedImage.TYPE_BYTE_GRAY);

		// calculate the result image from the three seperate rgb channels
		for (int i = 0; i < out.getWidth(); i++) {
			for (int j = 0; j < out.getHeight(); j++) {

				// get minimum from channels
				int min = 255;
				for (int k = 0; k < channels.length; k++) {
					if (min > channels[k].getPixel(i, j)[0])
						min = channels[k].getPixel(i, j)[0];
				}

				out.setRGB(i, j, (byte) min);
			}
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
		ImagePlus bImp = new ImagePlus("blue", channels[2]);
		return new ImagePlus[] { rImp, gImp, bImp };
	}

	private static ImageStack[] splitRGB(ImageStack rgb, boolean keepSource) {
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
