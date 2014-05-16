package ch.fhnw.imvs8.businesscardreader.ner.stemming;

/**
 * Strategy Pattern for the stemming
 * 
 * @author jon
 *
 */
public interface StemmingStrategy {

	/**
	 * So stemming for given input word
	 * @param input input word to do stemming
	 * @return output, stemmed word
	 */
	public String stemWord(String input);
}
