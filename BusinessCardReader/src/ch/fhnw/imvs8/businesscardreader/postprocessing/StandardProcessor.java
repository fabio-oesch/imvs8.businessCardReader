package ch.fhnw.imvs8.businesscardreader.postprocessing;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import ch.fhnw.imvs8.businesscardreader.BusinessCardField;
import ch.fhnw.imvs8.businesscardreader.ner.LabeledWord;
import ch.fhnw.imvs8.businesscardreader.ocr.AnalysisResult;

/**
 *
 * @author jon
 */
public class StandardProcessor implements Processor {
	private final String[] importantFields = {"FN", "LN","I-TN","EMA","ORG"};
	private final double nerConfidenceThreshold = 0.0;
	private final double ocrConfidenceThreshold = 60.0;
	
	public StandardProcessor() {
		
	}
	
	public enum Confidence {
		Correct,
		PossiblyWrong,
		WordContainsErrors
	}

	private void isFalse() {
		
	}
	
	private void isUnsure() {
		
	}
	
	@Override
	public Map<String, BusinessCardField> process(AnalysisResult ocrResult,Map<String, LabeledWord> nerResult) {
		HashMap<String,BusinessCardField> answer = new HashMap<>();
		
		for(int i = 0; i < LabeledWord.LABELS.length;i++) {
			if(LabeledWord.HUMAN_READABLE_LABELS[i] != null) {
				LabeledWord w = nerResult.get(LabeledWord.LABELS[i]);
				String humanLabel = LabeledWord.HUMAN_READABLE_LABELS[i];
				if(w != null) {
					
					answer.put(humanLabel, new BusinessCardField(w.getWordAsString(),w,humanLabel));
				} else {
					answer.put(humanLabel, new BusinessCardField(humanLabel));
				}
			}
		}

		
		return answer;
	}


}
