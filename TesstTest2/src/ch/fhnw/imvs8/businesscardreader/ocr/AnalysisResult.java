package ch.fhnw.imvs8.businesscardreader.ocr;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

/**
 * Represents the result of the OCR analysis.
 * It is responsible for metadata stuff
 * @author Jon
 *
 */
public class AnalysisResult {
	//meta infos
	private File image;
	//private String camera;
	
	private ArrayList<String> words;
	private ArrayList<Rectangle> boundingBoxes;
	private ArrayList<Float> confidences;
	
	/**
	 * @param image File 
	 * @param words 
	 * @param bBoxes
	 * @param conf
	 */
	public AnalysisResult(File image,ArrayList<String> words, ArrayList<Rectangle> bBoxes, ArrayList<Float> conf) {
		this.image = image;
		this.words = words;
		this.boundingBoxes = bBoxes;
		this.confidences = conf;
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
	public void readMetaInfo() {
		try {
			Metadata metadata = ImageMetadataReader.readMetadata(image);
			
			for (Directory directory : metadata.getDirectories()) {
				System.out.println(directory.getName());
			    for (Tag tag : directory.getTags()) {
			        System.out.println(tag);
			    }
			}
		} catch (ImageProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
}
