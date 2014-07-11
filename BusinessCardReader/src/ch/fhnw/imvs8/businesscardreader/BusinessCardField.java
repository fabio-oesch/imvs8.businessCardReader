package ch.fhnw.imvs8.businesscardreader;

import java.util.ArrayList;
import java.util.List;

import ch.fhnw.imvs8.businesscardreader.ner.LabeledWord;

public class BusinessCardField {
	private final String label;
	private final String field;
	private final String NERLabel;
	private boolean isWrong;
	private boolean isUnsure;
	
	
	public BusinessCardField(boolean isWrong, String NERLabel,String humanReadableLabel) {
		this.NERLabel = NERLabel;
		this.field = null;
		label = humanReadableLabel;
		this.isWrong = isWrong;
	}
	
	public BusinessCardField(String field,boolean isWrong,boolean isUnsure, String NERLabel, String humanReadableLabel) {
		this.NERLabel = NERLabel;
		this.field = field;
		label = humanReadableLabel;
		this.isWrong = isWrong;
		this.isUnsure = isUnsure;
		//only make important fields isWrong if they are empty
	}
	
	public boolean isWrong() {
		return isWrong;
	}
	
	public boolean isUnsure() {
		//only returns isUnsure if isWrong = false. 
		//because isWrong and isUnsure should not both be true at the same time. This BusinessCardField is either wrong, or either not sure if true but not both at the same time.
		return !isWrong ? isUnsure : false;
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
	
	public String getNERLabel() {
		return this.NERLabel;
	}
	
	public String getLabel() {
		return this.label;
	}
}
