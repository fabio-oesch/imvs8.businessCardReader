package ch.fhnw.imvs8.businesscardreader.ocr;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import net.sourceforge.tess4j.TessAPI1;
import net.sourceforge.tess4j.TessAPI1.TessPageIterator;
import net.sourceforge.tess4j.TessAPI1.TessPageIteratorLevel;
import net.sourceforge.tess4j.TessAPI1.TessResultIterator;
import net.sourceforge.vietocr.ImageIOHelper;
import ch.fhnw.imvs8.businesscardreader.imagefilters.FilterBundle;

import com.sun.jna.Pointer;

/**
 * Represents an OCR engine which is able to analyse an image and return an
 * AnalysisResult object.
 * 
 * @author Jon
 */
public class OCREngine {
	private static final double MINIMUM_DESKEW_THRESHOLD = 0.05d;
	private net.sourceforge.tess4j.TessAPI1.TessBaseAPI api;
	private FilterBundle bundle;

	public OCREngine() {
		bundle = null;
		api = TessAPI1.TessBaseAPICreate();

		// configuration
		TessAPI1.TessBaseAPIInit3(api, "tessdata", "eng");
		TessAPI1.TessBaseAPISetPageSegMode(api,
				TessAPI1.TessPageSegMode.PSM_AUTO);
	}

	public OCREngine(FilterBundle bundle) {
		this.bundle = bundle;
		api = TessAPI1.TessBaseAPICreate();

		// configuration
		TessAPI1.TessBaseAPIInit3(api, "tessdata", "eng");
		TessAPI1.TessBaseAPISetPageSegMode(api,
				TessAPI1.TessPageSegMode.PSM_AUTO);
	}

	/**
	 * analyze an image
	 * 
	 * @param im
	 *            to analyse
	 * @throws FileNotFoundException
	 */
	public AnalysisResult analyzeImage(File im) throws FileNotFoundException {
		AnalysisResult res = null;
		try {
			BufferedImage image = ImageIO.read(new FileInputStream(im)); // load
																			// image

			if (bundle != null)
				image = this.bundle.applyFilters(image);

			ByteBuffer buf = ImageIOHelper.convertImageData(image); // require
																	// jai-imageio
																	// lib to
																	// read TIFF

			// maybe not needed, but still here from the copied
			int bpp = image.getColorModel().getPixelSize(); // bit per pixel
			int bytespp = bpp / 8;
			int bytespl = (int) Math.ceil(image.getWidth() * bpp / 8.0);

			// analyze
			TessAPI1.TessBaseAPISetImage(this.api, buf, image.getWidth(),
					image.getHeight(), bytespp, bytespl);
			TessAPI1.TessBaseAPIRecognize(this.api, null);

			TessResultIterator ri = TessAPI1.TessBaseAPIGetIterator(this.api);
			TessPageIterator pi = TessAPI1
					.TessResultIteratorGetPageIterator(ri);

			res = this.runThroughResult(im, pi, ri);

		} catch (IOException e) {
			// well, we are screwed
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * Iterates over the tesseract result per word and puts them in a
	 * AnalysisResult object
	 * 
	 * @param im
	 * @param pi
	 * @param ri
	 * @return
	 */
	private AnalysisResult runThroughResult(File im,
			TessAPI1.TessPageIterator pi, TessResultIterator ri) {
		TessAPI1.TessPageIteratorBegin(pi);
		LinkedList<Float> confidences = new LinkedList<>();
		LinkedList<Rectangle> bBoxes = new LinkedList<>();
		LinkedList<String> words = new LinkedList<>();

		do {
			Pointer ptr = TessAPI1.TessResultIteratorGetUTF8Text(ri,
					TessPageIteratorLevel.RIL_WORD);

			// tesseract can return a null string, so if it did that, don't add
			// it
			if (ptr != null) {
				words.add(ptr.getString(0));
				float conf = TessAPI1.TessResultIteratorConfidence(ri,
						TessPageIteratorLevel.RIL_WORD);
				confidences.add(conf);

				IntBuffer leftB = IntBuffer.allocate(1);
				IntBuffer topB = IntBuffer.allocate(1);
				IntBuffer rightB = IntBuffer.allocate(1);
				IntBuffer bottomB = IntBuffer.allocate(1);
				TessAPI1.TessPageIteratorBoundingBox(pi,
						TessPageIteratorLevel.RIL_WORD, leftB, topB, rightB,
						bottomB);
				int left = leftB.get();
				int top = topB.get();
				int right = rightB.get();
				int bottom = bottomB.get();
				Rectangle r = new Rectangle(left, top, right - left, bottom
						- top);
				bBoxes.add(r);
			}

		} while (TessAPI1.TessPageIteratorNext(pi,
				TessAPI1.TessPageIteratorLevel.RIL_WORD) == TessAPI1.TRUE);

		return new AnalysisResult(im, new ArrayList<String>(words),
				new ArrayList<Rectangle>(bBoxes), new ArrayList<Float>(
						confidences));
	}

	private void deskew() {
		/*
		 * File imageFile = new File("eurotext_deskew.png"); BufferedImage bi =
		 * ImageIO.read(imageFile); ImageDeskew id = new ImageDeskew(bi); double
		 * imageSkewAngle = id.getSkewAngle(); // determine skew angle if
		 * ((imageSkewAngle > MINIMUM_DESKEW_THRESHOLD || imageSkewAngle <
		 * -(MINIMUM_DESKEW_THRESHOLD))) { bi = ImageHelper.rotateImage(bi,
		 * -imageSkewAngle); // deskew image }
		 */
	}

	public static void main(String[] args) throws Exception {
		// AnalysisResult res = new AnalysisResult(new File("htconex.jpg"));
		// res.readMetaInfo();
		/*
		 * GenericFilterBundle bundle = new GenericFilterBundle();
		 * bundle.appendFilter(new GrayScaleFilter()); OCREngine t = new
		 * OCREngine(bundle);
		 * 
		 * try { AnalysisResult res = t.analyzeImage(new File("eurotext.tif"));
		 * System.out.println(res.getResultSize()); res.readMetaInfo(); } catch
		 * (Exception e) {
		 * 
		 * }
		 */
	}
}
