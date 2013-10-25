package ch.fhnw.imvs8.businesscardreader.imagefilters;

import ij.ImagePlus;
import ij.plugin.ChannelSplitter;

import java.awt.image.BufferedImage;
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

	}

	@Override
	public BufferedImage applyFilters(BufferedImage im) {
		ImagePlus plus = new ImagePlus("filtered_image", im);

		ImagePlus[] channels = ChannelSplitter.split(plus);

		for (int i = 0; i < channels.length; i++)
			for (ImageFilter f : filters)
				channels[i] = f.filter(channels[i]);

		BufferedImage out = new BufferedImage(channels[0].getWidth(),
				channels[0].getHeight(), BufferedImage.TYPE_BYTE_GRAY);

		// copy content
		return out;
	}
}
