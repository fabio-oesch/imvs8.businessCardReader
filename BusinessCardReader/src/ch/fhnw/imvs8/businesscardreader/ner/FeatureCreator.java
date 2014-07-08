package ch.fhnw.imvs8.businesscardreader.ner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import ch.fhnw.imvs8.businesscardreader.ner.stemming.StemmingStrategy;
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
	private final StemmingStrategy strat;
	/**
	 * @param t Lookup tables to use in creating the features
	 */
	public FeatureCreator(LookupTables t, StemmingStrategy strategy) {
		this.tables = t;
		this.strat = strategy;
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
		int lines = res.getTotalNumberOfWords();
		for(int i = 0; i < res.getResultSize();i++) {
			w.write(this.createLine(res.getWord(i),res.getLineIndex(i),lines, res.getColumnIndex(i),res.getTotalNumberOfWordsInLine(i),res.getConfidence(i)));
			w.write("\n");
		}
		
		w.close();
		
		return tmpFile;
	}
	
	/**
	 * Here is the magic
	 * Every word gets a list of features attached to it and separated by a space. The output will be
	 * 
	 * "{word} {feature0} {feature1} {feature2}..."
	 * @param word
	 * @return String word with line of features for CRF++
	 */
	public String createLine(String word,int lineIndex,int totLines,int colIndex,int totColumns, double confidence) {
		String e = " ";
		String t = "1";		//true
		String f = "0"; 	//false
		StringBuilder out = new StringBuilder(word); out.append(e);
		
		String stemmedWord = strat.stemWord(word);
		
		//is in prename LUT feature
		out.append("fp"); if(tables.getPrenameSet().contains(stemmedWord)) out.append(t); else out.append(f);
		out.append(e);
		
		//is in lastname LUT feature
		out.append("lp"); if(tables.getLastnameSet().contains(stemmedWord)) out.append(t); else out.append(f);
		out.append(e);
		
		//is in roadname LUT feature
		out.append("st"); if(tables.getRoadnameSet().contains(stemmedWord)) out.append(t); else out.append(f);
		out.append(e);
		
		//is in zip LUT feature
		out.append("pc"); if(tables.getZipSet().contains(stemmedWord)) out.append(t); else out.append(f);
		out.append(e);
		
		//is in places LUT feature
		out.append("ci"); if(tables.getPlacesSet().contains(stemmedWord)) out.append(t); else out.append(f);
		out.append(e);
		
		//is a word a typical leading a mobile telephone number
		out.append("mw");if(tables.getMobileWordSet().contains(stemmedWord)) out.append(t); else out.append(f);
		out.append(e);
		
		//is word a typical mobile number prefix (for example 079)
		out.append("mpre");if(tables.getMobilePrefixSet().contains(stemmedWord)) out.append(t); else out.append(f);
		out.append(e);
		
		//is a word typically leading a fixnet telephone number
		out.append("tw");if(tables.getTelWordSet().contains(stemmedWord)) out.append(t); else out.append(f);
		out.append(e);
		
		//is a word typically leading a fixnet telephone number
		out.append("fw");if(tables.getFaxWordSet().contains(stemmedWord)) out.append(t); else out.append(f);
		out.append(e);
		
		//is word a typical fixnet number prefix (for example 055)
		out.append("fixpre");if(tables.getFixnetPrefixSet().contains(stemmedWord)) out.append(t); else out.append(f);
		out.append(e);
		
		//sets the numbers feature. There are currently 5 features dedicated to numbers. Each feature says if this word is a number and how many digits it has.
		if(stemmedWord.matches("-?\\d+")) {
			int length = stemmedWord.length();
			
			for(int i = 0; i < 4;i++) {
				out.append("nu");
				out.append(i+1);
				out.append("dig");
				if((i+1) ==length)
					out.append(t);
				else
					out.append(f);
					
				out.append(e);
			}
			
			//handle case if it is longer than 4
			if(stemmedWord.length() > 4) {
				out.append("nu4plusdig1 ");
			}
			else {
				out.append("nu4plusdig0 ");
			}
			
		}
		else {
			out.append("nu1dig0 nu2dig0 nu3dig0 nu4dig0 nu4plusdig0 ");
		}
		
		//contains an @
		out.append("em"); if(stemmedWord.contains("@")) out.append(t); else out.append(f);
		out.append(e);
		
		//word contains a domain like ".com" or starts with "www"
		out.append("we");
		List<String> domains = tables.getDomainsList();
		String found = f;
		for(String s : domains)
			if(stemmedWord.contains(s)) {
				found = t;
				break;
			}
		if(stemmedWord.startsWith("www"))
			found = t;
		out.append(found);
		out.append(e);
		
		//word contains known street identifiers
		out.append("stid");
		List<String> roadIdentifiers = tables.getRoadIdentifiers();
		found = f;
		for(String s : roadIdentifiers)
			if(stemmedWord.contains(s)) {
				found = t;
				break;
			}
		out.append(found);
		out.append(e);
		
		//word starts with country
		out.append("pcid");
		List<String> zipIdentifiers = tables.getZipIdentifiers();
		found = f;
		for(String s : zipIdentifiers)
			if(word.contains(s)) {
				found = t;
				break;
			}
		out.append(found);
		out.append(e);
		
		//word is an org identifier like ltd.
		out.append("orgid");if(tables.getORGIdentifierSet().contains(stemmedWord)) out.append(t); else out.append(f);
		out.append(e);
		
		//!!!!!! META Features
		
		//is word on the first line
		out.append("fline");if(lineIndex == 0) out.append(t); else out.append(f);
		out.append(e);
		
		//is word on the last line
		out.append("lline");if(lineIndex +1 == totLines) out.append(t); else out.append(f);
		out.append(e);
		
		//line index
		out.append(lineIndex);
		out.append(e);
		
		//is word in the first column
		out.append("fcol");if(colIndex  == 0) out.append(t); else out.append(f);
		out.append(e);
		
		//is word in the last column
		out.append("fline");if(colIndex +1 == totColumns) out.append(t); else out.append(f);
		out.append(e);
		
		//column index
		out.append(colIndex);
		out.append(e);
		
		//!!!!! tesseract - Confidence features
		out.append("clow"); if(confidence < 40.0) out.append(t); else out.append(f);
		out.append(e);
		out.append("c50"); if(confidence >= 40.0 && confidence < 50.0) out.append(t); else out.append(f);
		out.append(e);
		out.append("c60"); if(confidence >= 50.0 && confidence < 60.0) out.append(t); else out.append(f);
		out.append(e);
		out.append("c70"); if(confidence >= 60.0 && confidence < 70.0) out.append(t); else out.append(f);
		out.append(e);
		out.append("c80"); if(confidence >= 70.0 && confidence < 80.0) out.append(t); else out.append(f);
		out.append(e);
		out.append("chigh"); if(confidence >= 80.0) out.append(t); else out.append(f);
		out.append(e);
		
		return out.toString();
	}
}
