package ch.fhnw.imvs8.businesscardreader.ner.stemming;

/**
 * Strategy Pattern for the stemming in the NER Engine.
 * The implementation Classes are responsible for the Stemming in the NER Engine.
 * Stemming is used to reduce the number of Words in the dictionary.
 * 
 * @author jon
 *
 */
public interface StemmingStrategy {

	/**
	 * Do stemming for given input word
	 * @param input input word to do stemming
	 * @return output, stemmed word
	 */
	public String stemWord(String input);
}
