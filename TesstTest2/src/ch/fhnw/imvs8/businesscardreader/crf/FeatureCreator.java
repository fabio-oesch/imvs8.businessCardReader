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
	private final String tmpFile;
	private final LookupTables tables;
	
	/**
	 * 
	 * @param t Lookup tables to use
	 */
	public FeatureCreator(LookupTables t, String tmpFile) {
		this.tables = t;
		this.tmpFile = tmpFile;
	}
	
	/**
	 * 
	 * @param res AnalysisResult from OCREngine run
	 * @return relative file path for the CRF++ input
	 * @throws IOException 
	 */
	public String createFeatures(AnalysisResult res) throws IOException {
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
	 * @param word
	 * @return
	 */
	private String createLine(String word) {
		String e = " ";
		String t = "1";		//true
		String f = "0"; 	//false
		StringBuilder out = new StringBuilder(word); out.append(e);
		
		out.append("fp"); if(tables.getPrenameList().contains(word)) out.append(t); else out.append(f);
		out.append(e);
		
		out.append("lp"); if(tables.getLastnameList().contains(word)) out.append(t); else out.append(f);
		out.append(e);
		
		out.append("st"); if(tables.getRoadnameList().contains(word)) out.append(t); else out.append(f);
		out.append(e);
		
		out.append("pc"); if(tables.getZipList().contains(word)) out.append(t); else out.append(f);
		out.append(e);
		
		out.append("ci"); if(tables.getPlacesList().contains(word)) out.append(t); else out.append(f);
		out.append(e);
		
		
		
		
		return out.toString();
	}
}
