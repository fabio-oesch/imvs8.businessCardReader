package ch.fhnw.imvs8.businesscardreader.postprocessing;

import java.util.Map;

import ch.fhnw.imvs8.businesscardreader.BusinessCardField;
import ch.fhnw.imvs8.businesscardreader.ner.LabeledWord;

/**
 *
 * @author jon
 */
public class LabeledWordAnalyzer {
	public enum Confidence {
		Correct,
		PossiblyWrong,
		WordContainsErrors
	}

	public Map<String,BusinessCardField> process(Map<String,LabeledWord> words) {
		return null;
	}
}
