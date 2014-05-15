package ch.fhnw.imvs8.businesscardreader.crf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

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
		File f = new File(tmpFile);
		
		if(!f.exists())
			f.createNewFile();
		
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
		out.append("fp"); if(tables.getPrenameSet().contains(word)) out.append(t); else out.append(f);
		out.append(e);
		
		//is in lastname LUT feature
		out.append("lp"); if(tables.getLastnameSet().contains(word)) out.append(t); else out.append(f);
		out.append(e);
		
		//is in roadname LUT feature
		out.append("st"); if(tables.getRoadnameSet().contains(word)) out.append(t); else out.append(f);
		out.append(e);
		
		//is in zip LUT feature
		out.append("pc"); if(tables.getZipSet().contains(word)) out.append(t); else out.append(f);
		out.append(e);
		
		//is in places LUT feature
		out.append("ci"); if(tables.getPlacesSet().contains(word)) out.append(t); else out.append(f);
		out.append(e);
		
		//TODO: add other features, currently it just adds empty features in order to make it run
		out.append("mw0 tm0 ww0 tw0 fw0 tf0 ");
		
		//contains an @
		out.append("em"); if(word.contains("@")) out.append(t); else out.append(f);
		out.append(e);
		
		out.append("ti0 ");
		
		//word contains a domain like ".com"
		out.append("we");
		List<String> domains = tables.getDomainsList();
		String found = f;
		for(String s : domains)
			if(word.contains(s)) {
				found = t;
				break;
			}
		out.append(found);
		out.append(e);
		
		//
		
		out.append("cb0 ");
		
		//is a number
		out.append("nu");
		try{
			int number = Integer.parseInt(word);
			out.append(t);
		} catch(Exception ex) {
			out.append(f);
		}
		out.append(e);
		
		out.append("idk0");
		
		return out.toString();
	}
}
