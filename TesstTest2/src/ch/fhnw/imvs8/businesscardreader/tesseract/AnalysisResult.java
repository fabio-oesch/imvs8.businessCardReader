package ch.fhnw.imvs8.businesscardreader.tesseract;

import java.awt.Rectangle;
import java.io.File;

public class AnalysisResult {
	//meta info
	private File image;
	private String camera;
	
	private String word;
	private Rectangle boundingBox;
	private double confidence;
	
	public AnalysisResult() {
		
	}
}
