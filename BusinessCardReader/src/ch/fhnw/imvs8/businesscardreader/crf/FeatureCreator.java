package ch.fhnw.imvs8.businesscardreader.crf;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import ch.fhnw.imvs8.businesscardreader.ocr.AnalysisResult;

/**
 * Creates features from the AnalysisResult for a CRF analysis
 * 
 * It currently saves the feature list in a file so CRF++ can be called via console
 * @author jon
 *
 */
public class FeatureCreator {
	private final LookupTables tables;
	
	/**
	 * @param t Lookup tables to use in creating the features
	 */
	public FeatureCreator(LookupTables t) {
		this.tables = t;
	}
	
	/**
	 *  creates for each word a list of features and writes them in a file at tmpFile
	 *  
	 * @param res AnalysisResult from OCREngine run
	 * @param tmpFile path to output file
	 * @return relative file path for the CRF++ input
	 * 
	 * @throws IOException 
	 */
	public String createFeatures(AnalysisResult res,String tmpFile) throws IOException {
		BufferedWriter w = new BufferedWriter(new FileWriter(tmpFile));
		
		for(int i = 0; i < res.getResultSize();i++) {
			w.write(this.createLine(res.getWord(i)));
			w.write("\n");
		}
		
		w.close();
		
		return tmpFile;
	}
	
	/**
	 * Here is the magic
	 * Every word gets a list of features attached to it. the output will be
	 * 
	 * "{word} {feature0} {feature1} {feature2}..."
	 * @param word
	 * @return String word with line of features for CRF++
	 */
	private String createLine(String word) {
		String e = " ";
		String t = "1";		//true
		String f = "0"; 	//false
		StringBuilder out = new StringBuilder(word); out.append(e);
		
		//is in prename LUT feature
		out.append("fp"); if(tables.getPrenameList().contains(word)) out.append(t); else out.append(f);
		out.append(e);
		
		//is in lastname LUT feature
		out.append("lp"); if(tables.getLastnameList().contains(word)) out.append(t); else out.append(f);
		out.append(e);
		
		//is in roadname LUT feature
		out.append("st"); if(tables.getRoadnameList().contains(word)) out.append(t); else out.append(f);
		out.append(e);
		
		//is in zip LUT feature
		out.append("pc"); if(tables.getZipList().contains(word)) out.append(t); else out.append(f);
		out.append(e);
		
		//is in places LUT feature
		out.append("ci"); if(tables.getPlacesList().contains(word)) out.append(t); else out.append(f);
		out.append(e);
		
		
		
		
		return out.toString();
	}
}
