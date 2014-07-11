package ch.fhnw.imvs8.businesscardreader.ner;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents word labeled by the NEREngine class.
 * 
 * a word can consist of multiple subwords.
 * 
 * this class also holds the position in which each subword was found in the original text
 * 
 * It also contains a list of all possible labels;
 * 
 * This object is Immutable
 * @author jon
 *
 */
public class LabeledWord {
	public static final String[] LABELS = { "TIT","FN","LN","ST","PLZ","ORT","B-MN","I-MN","B-TN","I-TN","B-FN","I-FN","EMA","WEB","ORG","IDK"};
	public static final String[] HUMAN_READABLE_LABELS = { "Title","First Name", "Last Name", "Street", "Zip Code", "City", null," Mobile Number",null,"Fixnet Number", null, "Fax Number", "Email","Web", "Organisation","Unknown"};
	private double confidence;
	private final String label;
	private final List<String> subWords;
	private final List<Integer> subWordPositions;	//positions in the text, 0 for first.
	
	/**
	 * 
	 * @param label
	 * @param entity
	 * @param confidence
	 */
	public LabeledWord(String label, String entity, double confidence, int position) {
		this.label = label;
		this.confidence = confidence;
		subWords = new ArrayList<>();
		subWordPositions = new ArrayList<>();
		subWordPositions.add(position);
		subWords.add(entity);
	}

	/**
	 * adds a Word with the same label to this word.
	 * 
	 * @param e Add all subwords of e to this
	 */
	public void addWordAfter(LabeledWord e) {
		for(String s : e.subWords)
			subWords.add(s);
		for(Integer i: e.subWordPositions)
			subWordPositions.add(i);
		confidence = Math.min(confidence, e.confidence);
	}
	public double getConfidence() {
		return confidence;
	}

	public String getLabel() {
		return label;
	}

	/**
	 * Puts all Subwords in a single String
	 * @return
	 */
	public String getWordAsString() {
		StringBuilder b = new StringBuilder();
		for(String s :subWords) {
			b.append(s);
			b.append(" ");
		}
		
		return (b.toString()).trim();
	}
	
	public int getSubwordSize() {
		return subWords.size();
	}
	
	public String getSubword(int index) {
		if(index < 0 || index > this.subWords.size())
			return null;
		return subWords.get(index);
	}
	
	public int getSubwordPosition(int index) {
		if(index < 0 || index > this.subWords.size())
			return -1;
		return this.subWordPositions.get(index);
	}
	
	
}
