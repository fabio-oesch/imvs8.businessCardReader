package ch.fhnw.imvs8.businesscardreader.postprocessing;

import ch.fhnw.imvs8.businesscardreader.ner.LabeledWord;

/**
 * This object is responsible for analyzing a Labeled Word from the NER Process. It's output is a enumeration if the user can trust the output of the OCR and NER Process or if he should double check.
 * @author jon
 *
 */
public class LabeledWordAnalyzer {
	public enum Confidence {
		Correct,
		PossiblyWrong,
		WordContainsErrors
	}
	/**
	 * Analyze a Labeled Word from the NER Process
	 * @param word
	 * @return enumeration of how trustworthy these results are
	 * 	
	 */
	public Confidence analyze(LabeledWord word) {
		return Confidence.Correct;
	}
}
