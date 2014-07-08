package ch.fhnw.imvs8.businesscardreader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ch.fhnw.imvs8.businesscardreader.ner.LabeledWord;
import ch.fhnw.imvs8.businesscardreader.ocr.AnalysisResult;

public class BusinessCard {
	public static final String[] FIELD_NAMES = { "Title","First Name", "Last Name", "Street", "Zip Code", "City", " Mobile Number","Fixnet Number", "Fax Number", "Email","Web", "Organisation","Unknown"};
	private final Map<String,LabeledWord> nerResult;
	private final AnalysisResult ocrResult;
	private final Map<String,BusinessCardField> fields;
	
	public BusinessCard(Map<String,BusinessCardField> fields, AnalysisResult ocrResult,Map<String,LabeledWord> nerResult) {
		this.ocrResult = ocrResult;
		this.nerResult = nerResult;
		this.fields = fields;
	}
	
	public BusinessCardField getField(String fieldName) {
		return fields.get(fieldName);
	}
	
	public List<BusinessCardField> getAllFields() {
		return new ArrayList<BusinessCardField>(fields.values());
	}
	
	public Map<String,String> getFieldsAsStringMap() {
		HashMap<String,String> answer = new HashMap<>();
		for(Entry<String, BusinessCardField> entry : fields.entrySet())
			answer.put(entry.getKey(), entry.getValue().getWordAsString());
		
		return answer;
	}
	
	public String writeDebugOutput() {
		StringBuilder builder = new StringBuilder();

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
