package ch.fhnw.imvs8.businesscardreader;

import java.util.ArrayList;
import java.util.List;

import ch.fhnw.imvs8.businesscardreader.ner.LabeledWord;

/**
 * Represents a Field on the Businesscard
 * 
 * This Object contains the output of the Postprocessing Step in a easily accessible form.
 * @author jon
 */
public class BusinessCardField {
	private final String label;
	private final String field;
	private final String NERLabel;
	private boolean isWrong;
	private boolean isUnsure;
	
	/**
	 * 
	 * @param isWrong
	 * @param NERLabel
	 * @param humanReadableLabel
	 */
	public BusinessCardField(boolean isWrong, String NERLabel,String humanReadableLabel) {
		this.NERLabel = NERLabel;
		this.field = null;
		label = humanReadableLabel;
		this.isWrong = isWrong;
	}
	
	/**
	 * 
	 * @param field
	 * @param isWrong
	 * @param isUnsure
	 * @param NERLabel
	 * @param humanReadableLabel
	 */
	public BusinessCardField(String field,boolean isWrong,boolean isUnsure, String NERLabel, String humanReadableLabel) {
		this.NERLabel = NERLabel;
		this.field = field;
		label = humanReadableLabel;
		this.isWrong = isWrong;
		this.isUnsure = isUnsure;
		//only make important fields isWrong if they are empty
	}
	
	/**
	 * 
	 * @return true if this field contains an error
	 */
	public boolean isWrong() {
		return isWrong;
	}
	
	/**
	 * @return true if this field might be wrong
	 */
	public boolean isUnsure() {
		//only returns isUnsure if isWrong = false. 
		//because isWrong and isUnsure should not both be true at the same time. This BusinessCardField is either wrong, or either not sure if true but not both at the same time.
		return !isWrong ? isUnsure : false;
	}
	
	/**
	 * 
	 * @return The content of this field as String
	 */
	public String getField() {
		if(field != null)
			return field;
		return "";
	}
	
	/**
	 * 
	 * @return the label applied by the NER Process
	 */
	public String getNERLabel() {
		return this.NERLabel;
	}
	
	/**
	 * 
	 * @return the label of this field. For example: "Last Name"
	 */
	public String getLabel() {
		return this.label;
	}
}
