package ch.fhnw.imvs8.businesscardreader.postprocessing;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.fhnw.imvs8.businesscardreader.BusinessCardField;
import ch.fhnw.imvs8.businesscardreader.ner.LabeledWord;
import ch.fhnw.imvs8.businesscardreader.ocr.AnalysisResult;

/**
 *
 * @author jon
 */
public class StandardProcessor implements Processor {
	private final HashSet<String> importantFields;
	private final HashSet<String> telNumbers;
	private static final String plzField = "PLZ";
	private static final double nerConfidenceThreshold = 0.0;
	private static final double ocrConfidenceThreshold = 60.0;
	
	public StandardProcessor() {
		importantFields = new HashSet<>();
		importantFields.add("FN");importantFields.add("LN");importantFields.add("I-TN");importantFields.add("EMA");importantFields.add("ORG");
		telNumbers = new HashSet<>();
		telNumbers.add("I-TN");telNumbers.add("I-MN");telNumbers.add("I-FN");
	}
	
	private void clean(ArrayList<IntermediateField> fields) {
		
		for(int i = 0; i < fields.size();i++) {
			IntermediateField f = fields.get(i);
			if(telNumbers.contains(f.nerLabel)) {
				cleanNumber(f);
				cleanTelNumber(f);
			}
			
			if(f.equals(plzField))
				cleanNumber(f);
		}
	}
	
	private void cleanNumber(IntermediateField number) {
		number.cleanedWord.replaceAll("[Oo]", "0");
		number.cleanedWord.replaceAll("[iIlL]", "1");
	}
	
	private void cleanTelNumber(IntermediateField number) {
		
	}
	
	private void checkFalse(ArrayList<IntermediateField> fields) {
		checkMissing(fields);
		
		for(int i = 0; i < fields.size();i++) {
			IntermediateField f = fields.get(i);
			if(!f.isFalse) {
				switch(f.nerLabel) 
				{
					case "I-TN":
					case "I-FN":
					case "I-MN":
					case "PLZ":
						f.isFalse = Pattern.matches("\\w+", f.cleanedWord);
						break;
					case "FN":
					case "LN":
					case "ORT":
						f.isFalse = Pattern.matches("\\d+", f.cleanedWord);
						break;
					case "WEB":
						try {
							URL u = new URL(f.cleanedWord);
						} catch (MalformedURLException e) {
							f.isFalse = true;
						}
						break;
					default:
						break;
				}
			}
		}
		//numbers contains words
		//special characters
		
		
		//field web is not a valid url
	}
	
	/**
	 * Check if an important field is missing. If so mark it as "false"
	 * @param fields
	 */
	private void checkMissing(ArrayList<IntermediateField> fields) {
		for(IntermediateField f : fields) {
			System.out.println(f.nerLabel);
			System.out.println(f.word == null);
			if(importantFields.contains(f.nerLabel)) { 
				f.isFalse = f.word == null;
				System.out.println(f.isFalse);
			}
		}
	}
	
	private void checkUnsure(AnalysisResult ocrResult,ArrayList<IntermediateField> fields) {
		for(IntermediateField f: fields) {
			double ocrConfidence = 100.0;
			
			if(f.word != null) {
				LabeledWord w = f.word;
				
				for(int i = 0; i < w.getSubwordSize();i++) {
					double conf = ocrResult.getConfidence(w.getSubwordPosition(i));
					ocrConfidence = Math.min(ocrConfidence,conf);
				}
			}
			boolean bla = ocrConfidence <= ocrConfidenceThreshold;
			f.isUnsure = ocrConfidence <= ocrConfidenceThreshold;
		}
	}
	
	@Override
	public Map<String, BusinessCardField> process(AnalysisResult ocrResult,Map<String, LabeledWord> nerResult) {
		HashMap<String,BusinessCardField> answer = new HashMap<>();
		
		ArrayList<IntermediateField> fields = new ArrayList<>(nerResult.size());
		for(int i = 0; i < LabeledWord.LABELS.length;i++) {
			if(LabeledWord.HUMAN_READABLE_LABELS[i] != null) {
				IntermediateField f = new IntermediateField();
				f.word = nerResult.get(LabeledWord.LABELS[i]);
				f.cleanedWord = f.word == null ? "" : f.word.getWordAsString();
				f.nerLabel = LabeledWord.LABELS[i];
				f.humanLabel = LabeledWord.HUMAN_READABLE_LABELS[i];
				f.isFalse = false;
				f.isUnsure = false;
				fields.add(f);
			}
		}
		clean(fields);
		checkFalse(fields);
		checkUnsure(ocrResult,fields);
		
		for(IntermediateField f : fields) {
			if(f.word != null)
				answer.put(f.humanLabel, new BusinessCardField(f.cleanedWord,f.isFalse,f.isUnsure,f.nerLabel,f.humanLabel));
			else 
				answer.put(f.humanLabel, new BusinessCardField(f.isFalse,f.nerLabel,f.humanLabel));
		}

		return answer;
	}
	
	
	private class IntermediateField{
		public LabeledWord word;
		public String cleanedWord;
		public String nerLabel;
		public String humanLabel;
		public boolean isFalse;
		public boolean isUnsure;
	}
}
