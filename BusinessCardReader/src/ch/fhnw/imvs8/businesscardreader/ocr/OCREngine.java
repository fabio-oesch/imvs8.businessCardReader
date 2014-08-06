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
import net.sourceforge.vietocr.ImageHelper;
import net.sourceforge.vietocr.ImageIOHelper;
import ch.fhnw.imvs8.businesscardreader.imagefilters.Preprocessor;

import com.recognition.software.jdeskew.ImageDeskew;
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
	private Preprocessor processor;
	private boolean debugEnabled = false;

	public OCREngine() {
		this(null);
	}

	public OCREngine(Preprocessor p) {
		System.setProperty("jna.library.path", "/usr/local/lib");
		this.processor = p;
		api = TessAPI1.TessBaseAPICreate();

		// configuration
		TessAPI1.TessBaseAPIInit3(api, "tessdata", "deu");
		TessAPI1.TessBaseAPISetPageSegMode(api, TessAPI1.TessPageSegMode.PSM_SINGLE_BLOCK);
	}

	/**
	 * Enables the debug mode. It writes the preprocessed images to the same
	 * folder as the input image as "{filename}_debug.png"
	 */
	public void enableDebugMode() {
		this.debugEnabled = true;
	}

	/**
	 * analyze this image
	 * 
	 * @param im
	 *            to analyse
	 * @throws FileNotFoundException
	 */
	public OcrResult analyzeImage(File im) throws FileNotFoundException {
		OcrResult res = null;
		if (!im.exists()) {
			throw new FileNotFoundException(im.getAbsolutePath());
		}
		try {
			BufferedImage image = ImageIO.read(new FileInputStream(im)); // loadimage
			if (processor != null)
				image = this.processor.process(image);

			//image = this.deskew(image);
			if (this.debugEnabled)
				ImageIO.write(image, "png", new File(im.getAbsoluteFile() + "_debug.png"));

			ByteBuffer buf = ImageIOHelper.convertImageData(image); // require
																	// jai-imageio
																	// lib to
																	// read TIFF

			// maybe not needed, but still here from the copied
			int bpp = image.getColorModel().getPixelSize(); // bit per pixel
			int bytespp = bpp / 8;
			int bytespl = (int) Math.ceil(image.getWidth() * bpp / 8.0);

			// analyze
			TessAPI1.TessBaseAPISetImage(this.api, buf, image.getWidth(), image.getHeight(), bytespp, bytespl);
			TessAPI1.TessBaseAPIRecognize(this.api, null);

			TesseractLine lines = this.getLines();
			res = this.getWords(im,lines);
			
		} catch (IOException e) {
			// well, we are screwed
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * Iterates over the lines of tesseract. It doesn't delete the results, so you can iterate over the same results again
	 * 
	 * @param im
	 * @return
	 */
	private TesseractLine getLines() {
		TessResultIterator ri = TessAPI1.TessBaseAPIGetIterator(this.api);
		TessPageIterator pi = TessAPI1.TessResultIteratorGetPageIterator(ri);
		
		TessAPI1.TessPageIteratorBegin(pi);
		LinkedList<Rectangle> bBoxes = new LinkedList<>();
		LinkedList<String> textLines = new LinkedList<>();

		do {
			Pointer ptr = TessAPI1.TessResultIteratorGetUTF8Text(ri, TessPageIteratorLevel.RIL_TEXTLINE);

			// tesseract can return a null string, so if it did that, don't add
			// it
			if (ptr != null) {
				String line = ptr.getString(0);
				if(line != null && !"".equals(line.trim())) {
					textLines.add(line);
	
					IntBuffer leftB = IntBuffer.allocate(1);
					IntBuffer topB = IntBuffer.allocate(1);
					IntBuffer rightB = IntBuffer.allocate(1);
					IntBuffer bottomB = IntBuffer.allocate(1);
					TessAPI1.TessPageIteratorBoundingBox(pi, TessPageIteratorLevel.RIL_TEXTLINE, leftB, topB, rightB, bottomB);
					int left = leftB.get();
					int top = topB.get();
					int right = rightB.get();
					int bottom = bottomB.get();
					Rectangle r = new Rectangle(left, top, right - left, bottom - top);
					bBoxes.add(r);
				}
			}

		} while (TessAPI1.TessPageIteratorNext(pi, TessAPI1.TessPageIteratorLevel.RIL_TEXTLINE) == TessAPI1.TRUE);

		TesseractLine lines = new TesseractLine();
		lines.boundingBoxes = new ArrayList<>(bBoxes);
		lines.textLines = new ArrayList<>(textLines);
		return lines;
	}
	
	/**
	 * Returns the words of an 
	 * @param im
	 * @param lines
	 * @return
	 */
	private OcrResult getWords(File im,TesseractLine lines) {
		TessResultIterator ri = TessAPI1.TessBaseAPIGetIterator(this.api);
		TessPageIterator pi = TessAPI1.TessResultIteratorGetPageIterator(ri);
		
		TessAPI1.TessPageIteratorBegin(pi);
		LinkedList<Float> confidences = new LinkedList<>();
		LinkedList<Rectangle> bBoxes = new LinkedList<>();
		LinkedList<String> words = new LinkedList<>();

		do {
			Pointer ptr = TessAPI1.TessResultIteratorGetUTF8Text(ri, TessPageIteratorLevel.RIL_WORD);
			
			// tesseract can return a null string, so if it did that, don't add
			// it
			if (ptr != null) {
				String word = ptr.getString(0);
				if (word!=null && !word.trim().isEmpty()) {
						
					words.add(word);
					float conf = TessAPI1.TessResultIteratorConfidence(ri, TessPageIteratorLevel.RIL_WORD);
					confidences.add(conf);
	
					IntBuffer leftB = IntBuffer.allocate(1);
					IntBuffer topB = IntBuffer.allocate(1);
					IntBuffer rightB = IntBuffer.allocate(1);
					IntBuffer bottomB = IntBuffer.allocate(1);
					TessAPI1.TessPageIteratorBoundingBox(pi, TessPageIteratorLevel.RIL_WORD, leftB, topB, rightB, bottomB);
					int left = leftB.get();
					int top = topB.get();
					int right = rightB.get();
					int bottom = bottomB.get();
					Rectangle r = new Rectangle(left, top, right - left, bottom - top);
					bBoxes.add(r);
				}

				TessAPI1.TessDeleteText(ptr);
			}

		} while (TessAPI1.TessPageIteratorNext(pi, TessAPI1.TessPageIteratorLevel.RIL_WORD) == TessAPI1.TRUE);

		return new OcrResult(im, new ArrayList<String>(words), new ArrayList<Rectangle>(bBoxes), new ArrayList<Float>(confidences),lines);
	}

	/**
	 * Uses the Tess4j library to deskew an image It works nicely most of the
	 * times. But implementing this kills our testing framework. More work is
	 * needed to implement deskew, so currently not in use.
	 * 
	 * @param bi
	 * @return
	 */
	private BufferedImage deskew(BufferedImage bi) {
		ImageDeskew id = new ImageDeskew(bi);
		double imageSkewAngle = id.getSkewAngle(); // determine skew angle if
		if (imageSkewAngle > MINIMUM_DESKEW_THRESHOLD || imageSkewAngle < -MINIMUM_DESKEW_THRESHOLD) {
			return ImageHelper.rotateImage(bi, -imageSkewAngle); // deskew image
		}

		return bi;
	}

}
