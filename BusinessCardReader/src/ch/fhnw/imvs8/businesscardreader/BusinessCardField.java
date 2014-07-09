package ch.fhnw.imvs8.businesscardreader;

import java.util.ArrayList;
import java.util.List;

import ch.fhnw.imvs8.businesscardreader.ner.LabeledWord;

public class BusinessCardField {
	private final String label;
	@Deprecated
	private final LabeledWord word;
	private final String field;
	private boolean isWrong;
	private boolean isUnsure;
	
	
	public BusinessCardField(String humanReadableLabel) {
		this.word = null;
		this.field = null;
		label = humanReadableLabel;
	}
	
	public BusinessCardField(String field, LabeledWord w, String humanReadableLabel) {
		this.word = w;
		this.field = field;
		label = humanReadableLabel;
		//only make important fields isWrong if they are empty
	}
	
	public boolean isWrong() {
		return isWrong;
	}
	
	public boolean isUnsure() {
		return isUnsure;
	}
	
	/**
	 * Puts all Subwords in a single String, can be deleted?
	 * @return
	 */
	public String getField() {
		if(field != null)
			return field;
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
		if(word == null || index < 0 || index > word.getSubwordSize())
			return null;
		return word.getSubword(index)+";"+word.getSubwordPosition(index);
	}
}
