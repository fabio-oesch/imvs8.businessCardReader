package ch.fhnw.imvs8.businesscardreader.ner.stemming;

/**
 * This is a simple german stemming Object.
 * @author jon
 *
 */
public class SimpleGermanStemming implements StemmingStrategy {

	@Override
	public String stemWord(String input) {
		input = input.toLowerCase();
		input = input.replace("ä", "ae");
		input = input.replace("ö", "oe");
		input = input.replace("ü", "ue");
		return input.trim();
	}
}
