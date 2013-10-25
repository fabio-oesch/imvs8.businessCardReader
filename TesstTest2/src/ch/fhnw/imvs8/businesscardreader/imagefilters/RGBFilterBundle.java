package ch.fhnw.imvs8.businesscardreader.imagefilters;

import ij.ImagePlus;
import ij.plugin.ChannelSplitter;

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
	List<ImageFilter> filters;

	public RGBFilterBundle() {
		filters = new ArrayList<>(2);
		filters.add(new LightFilter());
		filters.add(new AutoBinaryFilter());
	}

	@Override
	public BufferedImage applyFilters(BufferedImage im) {
		ImagePlus plus = new ImagePlus("filtered_image", im);
		ImagePlus[] channels = ChannelSplitter.split(plus);

		// filter
		for (int i = 0; i < channels.length; i++)
			for (ImageFilter f : filters)
				channels[i] = f.filter(channels[i]);

		BufferedImage out = new BufferedImage(channels[0].getWidth(),
				channels[0].getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		BufferedImage[] bufChannels = new BufferedImage[channels.length];

		// create output
		for (int i = 0; i < out.getWidth(); i++)
			for (int j = 0; j < out.getHeight(); j++) {

				// get minimum from channels
				int min = 255;
				for (int k = 0; i < bufChannels.length; k++)
					if (min > bufChannels[k].getRGB(i, j))
						min = bufChannels[k].getRGB(i, j);

				out.setRGB(i, j, min);
			}

		return out;
	}
}
