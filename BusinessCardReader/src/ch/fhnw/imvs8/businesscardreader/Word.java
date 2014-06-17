package ch.fhnw.imvs8.businesscardreader;

import java.util.ArrayList;
import java.util.List;

import ch.fhnw.imvs8.businesscardreader.ner.LabeledWord;

public class Word {
	private final String label;
	private final LabeledWord word;
	private boolean isWrong;
	private boolean isUnsure;
	
	public Word(LabeledWord w, String humanReadableLabel) {
		word = w;
		label = humanReadableLabel;
	}
	
	public boolean isWrong() {
		return isWrong;
	}
	
	public boolean isUnsure() {
		return isUnsure;
	}
	
	/**
	 * Puts all Subwords in a single String
	 * @return
	 */
	public String getWordAsString() {
		if(word != null)
			return word.getWordAsString();
		return "";
	}
	
	public int getSubwordSize() {
		if(word != null)	
			return word.getSubwordSize();
		else
			return 0;
	}
	
	public String getNERLabel() {
		if(word != null)
			return word.getLabel();
		return "";
	}
	
	public String getLabel() {
		return this.label;
	}
	/**
	 * 
	 * @param index
	 * @return
	 */
	public String getSubwordAndPosition(int index) {
		if(word != null || index < 0 || index > word.getSubwordSize())
			return null;
		return word.getSubword(index)+";"+word.getSubwordPosition(index);
	}
}
