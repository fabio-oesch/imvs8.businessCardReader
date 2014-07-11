package ch.fhnw.imvs8.businesscardreader.postprocessing;

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
;
/**
 *
 * @author jon
 */
public class StandardProcessor implements Processor {
	private final HashSet<String> importantFields;
	private final double nerConfidenceThreshold = 0.0;
	private final double ocrConfidenceThreshold = 60.0;
	
	public StandardProcessor() {
		importantFields = new HashSet<>();
		importantFields.add("FN");importantFields.add("LN");importantFields.add("I-TN");importantFields.add("EMA");importantFields.add("ORG");
	}

	private void checkFalse(ArrayList<IntermediateField> fields) {
		checkMissing(fields);
		
		//numbers contains words
		//special characters
		//field web is not a valid url
		//field email not a valid email
	}
	
	/**
	 * Check if an important field is missing. If so mark it as "false"
	 * @param fields
	 */
	private void checkMissing(ArrayList<IntermediateField> fields) {
		for(IntermediateField f : fields) {
			if(importantFields.contains(f.nerLabel)) { 
				if(f.word == null)
					f.isFalse = true;
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
					Math.min(ocrConfidence,conf);
				}
			}
			
			f.isUnsure = ocrConfidence > ocrConfidenceThreshold ? true : false;
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
				System.out.println(f.word);
				f.nerLabel = LabeledWord.LABELS[i];
				f.humanLabel = LabeledWord.HUMAN_READABLE_LABELS[i];
				f.isFalse = false;
				f.isUnsure = false;
				fields.add(f);
			}
		}
		
		checkFalse(fields);
		checkUnsure(ocrResult,fields);
		
		for(IntermediateField f : fields) {
			if(f.word != null)
				answer.put(f.humanLabel, new BusinessCardField(f.word.getWordAsString(),f.isFalse,f.isUnsure,f.nerLabel,f.humanLabel));
			else 
				answer.put(f.humanLabel, new BusinessCardField(f.isFalse,f.nerLabel,f.humanLabel));
		}

		
		return answer;
	}
	
	public static void main(String[] args) {
		StandardProcessor p = new StandardProcessor();
		
		LabeledWord fn = new LabeledWord("FN","Max",90.0,0);
		LabeledWord ema = new LabeledWord("EMA","bla@max.bla",90.0,1);
		Map<String,LabeledWord> ner = new HashMap<>();
		ner.put("FN", fn);
		ner.put("EMA", ema);
		
		Map<String, BusinessCardField> fields = p.process(null, ner);
		for(BusinessCardField f : fields.values()) {
			System.out.println(f.getNERLabel()+" "+f.getField()+" isWrong:"+f.isWrong()+" isUnsure:"+f.isUnsure());
		}
		
	}
	

	private class IntermediateField{
		public LabeledWord word;
		public String nerLabel;
		public String humanLabel;
		public boolean isFalse;
		public boolean isUnsure;
	}
}
