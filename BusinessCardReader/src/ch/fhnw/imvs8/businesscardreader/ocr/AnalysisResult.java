package ch.fhnw.imvs8.businesscardreader.ocr;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;

/**
 * Represents the result of the OCR analysis. It is responsible for metadata
 * stuff
 * 
 * @author Jon
 * 
 */
public class AnalysisResult {
	private final ArrayList<String> words;
	private final ArrayList<Rectangle> boundingBoxes;
	private final ArrayList<Float> confidences;

	/**
	 * @param image
	 *          Image 
	 * @param words
	 * 			List words 
	 * @param bBoxes
	 * 			List of Bounding boxes per word
	 * @param conf
	 * 			List of confidence values per word
	 */
	public AnalysisResult(File image, ArrayList<String> words, ArrayList<Rectangle> bBoxes, ArrayList<Float> conf) {
		this.words = words;
		this.boundingBoxes = bBoxes;
		this.confidences = conf;
		//this.cleanResults();
	}

	public int getResultSize() {
		return words.size();
	}

	public String getWord(int index) {
		return words.get(index);
	}

	public Rectangle getBoundingBox(int index) {
		return boundingBoxes.get(index);
	}

	public Float getConfidence(int index) {
		return confidences.get(index);
	}
}
