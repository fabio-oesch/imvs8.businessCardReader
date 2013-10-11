package ch.fhnw.imvs8.businesscardreader.tesseract;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import com.sun.jna.Pointer;

import net.sourceforge.tess4j.TessAPI1.TessPageIterator;
import net.sourceforge.tess4j.TessAPI1.TessPageIteratorLevel;
import net.sourceforge.tess4j.TessAPI1.TessResultIterator;
import net.sourceforge.tess4j.TessAPI1;
import net.sourceforge.vietocr.ImageIOHelper;

public class TessDebugRun {
	private net.sourceforge.tess4j.TessAPI1.TessBaseAPI api;
	private AbstractImageFilter filters;
	
	public TessDebugRun(AbstractImageFilter filters) {
		this.filters = filters;
		api = TessAPI1.TessBaseAPICreate();
		
		//configuration
		TessAPI1.TessBaseAPIInit3(api, "tessdata", "eng");
        TessAPI1.TessBaseAPISetPageSegMode(api, TessAPI1.TessPageSegMode.PSM_AUTO);
	}
	
	private void runThroughResult(TessAPI1.TessPageIterator pi, TessResultIterator ri) {
		TessAPI1.TessPageIteratorBegin(pi);
		
		do {
			Pointer ptr = TessAPI1.TessResultIteratorGetUTF8Text(ri, TessPageIteratorLevel.RIL_WORD);
			String word = ptr.getString(0);
			float conf = TessAPI1.TessResultIteratorConfidence(ri, TessPageIteratorLevel.RIL_WORD);
			
			IntBuffer leftB = IntBuffer.allocate(1);
			IntBuffer topB = IntBuffer.allocate(1);
			IntBuffer rightB = IntBuffer.allocate(1);
			IntBuffer bottomB = IntBuffer.allocate(1);
			TessAPI1.TessPageIteratorBoundingBox(pi, TessPageIteratorLevel.RIL_WORD, leftB, topB, rightB, bottomB);
			int left = leftB.get();
			int top = topB.get();
			int right = rightB.get();
			int bottom = bottomB.get();
			
			//TODO: here you have all info you need, now do what you want
			
		} while (TessAPI1.TessPageIteratorNext(pi, TessAPI1.TessPageIteratorLevel.RIL_WORD) == TessAPI1.TRUE);
	}
	
	/**
	 * analyze an image
	 * @param im
	 * @throws FileNotFoundException
	 */
	public void analyzeImage(File im) throws FileNotFoundException{
		//load image
		
		BufferedImage image = null;
		try {
			image = ImageIO.read(new FileInputStream(im));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		ByteBuffer buf = ImageIOHelper.convertImageData(image);	// require jai-imageio lib to read TIFF
		int bpp = image.getColorModel().getPixelSize();			//bit per pixel
		int bytespp = bpp / 8;
		int bytespl = (int) Math.ceil(image.getWidth() * bpp / 8.0);
		
		//analyze
        TessAPI1.TessBaseAPISetImage(this.api, buf, image.getWidth(), image.getHeight(), bytespp, bytespl);
        TessAPI1.TessBaseAPIRecognize(this.api, null);
		TessResultIterator ri = TessAPI1.TessBaseAPIGetIterator(this.api);
		TessPageIterator pi = TessAPI1.TessResultIteratorGetPageIterator(ri);
		
		this.runThroughResult(pi, ri);	
	}
	
	public static void main(String[] args) {
		AnalysisResult res = new AnalysisResult(new File("htconex.jpg"));
		res.readMetaInfo();
		TessDebugRun t = new TessDebugRun(null);
		try {t.analyzeImage(new File("eurotext.tif")); }
		catch (Exception e) 
		{
			
		}
	}
}
