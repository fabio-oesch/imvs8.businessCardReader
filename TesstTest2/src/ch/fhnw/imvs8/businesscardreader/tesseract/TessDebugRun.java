package ch.fhnw.imvs8.businesscardreader.tesseract;

import java.io.File;
import java.nio.IntBuffer;

import com.sun.jna.Pointer;

import net.sourceforge.tess4j.TessAPI1.TessPageIterator;
import net.sourceforge.tess4j.TessAPI1.TessPageIteratorLevel;
import net.sourceforge.tess4j.TessAPI1.TessResultIterator;
import net.sourceforge.tess4j.TessAPI1;
import net.sourceforge.tess4j.Tesseract1;

public class TessDebugRun {
	net.sourceforge.tess4j.TessAPI1.TessBaseAPI api;
	
	public TessDebugRun() {
		api = TessAPI1.TessBaseAPICreate();

		//path maybe null?
		
		//load image
		/*File tiff = new File("eurotext.tif");
        BufferedImage image = ImageIO.read(new FileInputStream(tiff)); // require jai-imageio lib to read TIFF
        ByteBuffer buf = ImageIOHelper.convertImageData(image);
        int bpp = image.getColorModel().getPixelSize();
        int bytespp = bpp / 8;
        int bytespl = (int) Math.ceil(image.getWidth() * bpp / 8.0);*/
		
		
		TessAPI1.TessBaseAPIInit3(api, "tessdata", "eng");
        /*TessAPI1.TessBaseAPISetPageSegMode(handle, TessAPI1.TessPageSegMode.PSM_AUTO);
        TessAPI1.TessBaseAPISetImage(handle, buf, image.getWidth(), image.getHeight(), bytespp, bytespl);
        TessAPI1.TessBaseAPIRecognize(handle, null);*/
		TessResultIterator ri = TessAPI1.TessBaseAPIGetIterator(api);
		TessPageIterator pi = TessAPI1.TessResultIteratorGetPageIterator(ri);
		
		
		
		this.runThroughResult(pi, ri);		
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
			
			//TODO: here you have all info you need
			
		} while (TessAPI1.TessPageIteratorNext(pi, TessAPI1.TessPageIteratorLevel.RIL_WORD) == TessAPI1.TRUE);
	}
	
	public void analyzeImage(File im) {
		
	}


	public static void main(String[] args) {
		
	}
}
