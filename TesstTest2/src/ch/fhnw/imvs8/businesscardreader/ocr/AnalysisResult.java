package ch.fhnw.imvs8.businesscardreader.ocr;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

/**
 * Represents the result of the OCR analysis.
 * @author Jon
 *
 */
public class AnalysisResult {
	//meta infos
	private File image;
	//private String camera;
	
	private String[] words;
	private Rectangle[] boundingBoxes;
	private Float[] confidences;
	
	/**
	 * 
	 * @param image 
	 * @param words 
	 * @param bBoxes
	 * @param conf
	 */
	public AnalysisResult(File image,String[] words, Rectangle[] bBoxes, Float[] conf) {
		this.image = image;
		this.words = words;
		this.boundingBoxes = bBoxes;
		this.confidences = conf;
	}
	
	public int getResultSize() {
		return words.length;
	}
	
	public String getWord(int index) {
		if(index < words.length && index > 0)
			return words[index];
		return null;
	}
	
	public Rectangle getBoundingBox(int index) {
		if(index < boundingBoxes.length && index > 0 )
			return boundingBoxes[index];
		return null;
	}
	
	public Float getConfidence(int index) {
		if(index < confidences.length && index > 0) 
			return confidences[index];
		return null;
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
