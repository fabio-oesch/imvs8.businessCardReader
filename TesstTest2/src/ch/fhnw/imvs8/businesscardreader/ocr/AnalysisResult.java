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
	//meta infos
	private final File image;
	//private String camera;

	/*
	 * If a bounding box is larger than the average * (1+ignoreThreshold) or
	 * smaller than average*ignoreThreshold)it is probably trash and will be
	 * deleted
	 */
	private static final double ignoreThreshold = 0.2;
	private final ArrayList<String> words;
	private final ArrayList<Rectangle> boundingBoxes;
	private final ArrayList<Float> confidences;

	/**
	 * @param image
	 *            File
	 * @param words
	 * @param bBoxes
	 * @param conf
	 */
	public AnalysisResult(File image, ArrayList<String> words, ArrayList<Rectangle> bBoxes, ArrayList<Float> conf) {
		this.image = image;
		this.words = words;
		this.boundingBoxes = bBoxes;
		this.confidences = conf;
		this.cleanResults();
	}

	private void cleanResults() {
		double avHeight = 0;
		for (int i = 0; i < boundingBoxes.size(); i++) {
			Rectangle r = boundingBoxes.get(i);

			//account for 90 turned text
			double height = r.getHeight() <= r.getWidth() ? r.getHeight() : r.getWidth();
			avHeight += height;
		}
		avHeight /= boundingBoxes.size();

		double minThreshold = avHeight * ignoreThreshold;
		double maxThreshold = avHeight * (1 + ignoreThreshold);
		for (int i = 0; i < boundingBoxes.size(); i++) {
			Rectangle r = boundingBoxes.get(i);

			double height = r.getHeight() <= r.getWidth() ? r.getHeight() : r.getWidth();

			if (height > maxThreshold || height < minThreshold) {
				this.boundingBoxes.remove(i);
				this.confidences.remove(i);
				this.words.remove(i);
				i--;
			}
		}

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

	/**
	 * Debug method, should be removed eventually
	 */
	//	public void readMetaInfo() {
	//		try {
	//			Metadata metadata = ImageMetadataReader.readMetadata(image);
	//
	//			for (Directory directory : metadata.getDirectories()) {
	//				System.out.println(directory.getName());
	//				for (Tag tag : directory.getTags()) {
	//					System.out.println(tag);
	//				}
	//			}
	//		} catch (ImageProcessingException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		} catch (IOException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//
	//	}

}
