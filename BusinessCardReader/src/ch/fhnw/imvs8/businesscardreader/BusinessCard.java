package ch.fhnw.imvs8.businesscardreader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ch.fhnw.imvs8.businesscardreader.ner.LabeledWord;
import ch.fhnw.imvs8.businesscardreader.ocr.OcrResult;
/**
 * Represents a Business Card which was read.
 * It combines the Information of all steps in one Object.
 * @author jon
 *
 */
public class BusinessCard {
	public static final String[] FIELD_LABELS = { "Title","First Name", "Last Name", "Street", "Zip Code", "City", " Mobile Number","Telephone Number", "Fax Number", "Email","Web", "Organisation","Unknown"};
	private final Map<String,LabeledWord> nerResult;
	private final OcrResult ocrResult;
	private final Map<String,BusinessCardField> fields;
	
	public BusinessCard(Map<String,BusinessCardField> fields, OcrResult ocrResult,Map<String,LabeledWord> nerResult) {
		this.ocrResult = ocrResult;
		this.nerResult = nerResult;
		this.fields = fields;
	}
	
	/**
	 * 
	 * @param fieldLabel the Label of the Field. All possible Fields can be found in the static Array FIELD_LABELS
	 * @return BusinessCardField with this label.
	 *    This method only returns null, if fieldLabel was incorrect.
	 */
	public BusinessCardField getField(String fieldLabel) {
		return fields.get(fieldLabel);
	}
	
	/**
	 * 
	 * @return a list with all fields
	 */
	public List<BusinessCardField> getAllFields() {
		return new ArrayList<BusinessCardField>(fields.values());
	}
	
	/**
	 * 
	 * @return the data as a Map<NERLabel, Text>
	 */
	public Map<String,String> getFieldsAsStringMap() {
		HashMap<String,String> answer = new HashMap<>();
		for(Entry<String, BusinessCardField> entry : fields.entrySet())
			answer.put(entry.getValue().getNERLabel(), entry.getValue().getField());
		
		return answer;
	}
	
	/**
	 *  This method is responsible for writing the debug output from the OCR and the NER step.
	 *  This output can later be used as Trainingdata for the NER Engine.
	 * @return string containing all relevant information from the OCR Engine and the label set by the NER engine.
	 */
	public String writeDebugOutput() {
		StringBuilder builder = new StringBuilder();

		//encode
		Map<Integer,String> inverseLabels = new HashMap<>();
		for(LabeledWord w: nerResult.values()) {
			String label = w.getLabel();
			for(int i = 0; i < w.getSubwordSize();i++) {
				inverseLabels.put(w.getSubwordPosition(i), label);
			}
		}

		//decode
		for(int i = 0; i < ocrResult.getResultSize();i++) {
			builder.append(ocrResult.getWord(i));
			builder.append(",");
			builder.append(ocrResult.getLineIndex(i));
			builder.append(",");
			builder.append(ocrResult.getColumnIndex(i));
			builder.append(",");
			builder.append(ocrResult.getTotalNumberOfColumnsInLine(i));
			builder.append(",");
			builder.append(ocrResult.getConfidence(i));
			builder.append(",");
			builder.append(inverseLabels.get(i));

			builder.append("\n");
		}
		
		return builder.toString();
	}
	
	
}
