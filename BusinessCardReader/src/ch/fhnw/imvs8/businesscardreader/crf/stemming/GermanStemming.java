package ch.fhnw.imvs8.businesscardreader.crf.stemming;

public class GermanStemming implements StemmingStrategy {

	@Override
	public String stemWord(String input) {
		input = input.toLowerCase();
		input = input.replace("ä", "ae");
		input = input.replace("ö", "oe");
		input = input.replace("ü", "ue");
		return input;
	}
}
