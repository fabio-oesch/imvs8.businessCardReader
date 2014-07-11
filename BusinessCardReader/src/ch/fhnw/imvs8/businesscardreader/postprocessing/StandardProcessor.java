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
		//important fields missing, special case for telephone number
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
				if(f.word == null || f.word.getWordAsString().equals(""));
					f.isFalse = true;
			}
		}
	}
	
	
	private void checkUnsure(ArrayList<IntermediateField> fields) {
		//confidences
	}
	
	@Override
	public Map<String, BusinessCardField> process(AnalysisResult ocrResult,Map<String, LabeledWord> nerResult) {
		HashMap<String,BusinessCardField> answer = new HashMap<>();
		
		ArrayList<IntermediateField> fields = new ArrayList<>(nerResult.size());
		for(int i = 0; i < LabeledWord.LABELS.length;i++) {
			if(LabeledWord.HUMAN_READABLE_LABELS[i] != null) {
				IntermediateField f = new IntermediateField();
				f.word = nerResult.get(LabeledWord.LABELS[i]);
				f.nerLabel = LabeledWord.LABELS[i];
				f.humanLabel = LabeledWord.HUMAN_READABLE_LABELS[i];
				f.isFalse = false;
				f.isUnsure = false;
				fields.add(f);
			}
		}
		
		checkFalse(fields);
		checkUnsure(fields);
		
		for(IntermediateField f : fields) {
			if(f.word != null)
				answer.put(f.humanLabel, new BusinessCardField(f.word.getWordAsString(),f.isFalse,f.isUnsure,f.nerLabel,f.humanLabel));
			else 
				answer.put(f.humanLabel, new BusinessCardField(f.isFalse,f.nerLabel,f.humanLabel));
		}

		
		return answer;
	}

	private class IntermediateField{
		public LabeledWord word;
		public String nerLabel;
		public String humanLabel;
		public boolean isFalse;
		public boolean isUnsure;
	}
}
