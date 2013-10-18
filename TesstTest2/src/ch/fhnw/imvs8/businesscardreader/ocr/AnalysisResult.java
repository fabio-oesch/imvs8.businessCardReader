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
	//meta info
	private File image;
	//private String camera;
	
	private String word;
	private Rectangle boundingBox;
	private double confidence;
	
	public AnalysisResult(File image) {
		this.image = image;
	}
	
	/**
	 * Debug method, should be removed eventually
	 */
	public void readMetaInfo() {
		try {
			Metadata metadata = ImageMetadataReader.readMetadata(image);
			
			for (Directory directory : metadata.getDirectories()) {
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
