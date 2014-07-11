package ch.imvs8.businesscardreader.postprocessing;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import ch.fhnw.imvs8.businesscardreader.BusinessCardField;
import ch.fhnw.imvs8.businesscardreader.ner.LabeledWord;
import ch.fhnw.imvs8.businesscardreader.ocr.AnalysisResult;
import ch.fhnw.imvs8.businesscardreader.postprocessing.StandardProcessor;

public class StandardProcessorTest {

	@Test
	public void test() {
		StandardProcessor p = new StandardProcessor();
		
		LabeledWord fn = new LabeledWord("FN","Max",90.0,0);
		LabeledWord ln = new LabeledWord("LN","Muster",80.0,1);
		LabeledWord street = new LabeledWord("ST","Seidenstrasse",80.0,2);
		street.addWordAfter(new LabeledWord("ST","11",40.0,3));
		LabeledWord plz = new LabeledWord("PLZ", "5200", 45.0, 4);
		LabeledWord ort = new LabeledWord("ORT","Brugg",70,5);
		LabeledWord ema = new LabeledWord("EMA","max.muster@mail.com",90.0,6);
		
		ArrayList<String> words = new ArrayList<String>();
		words.add("Max");words.add("Muster");words.add("Seidenstrasse");words.add("11");words.add("5200");words.add("Brugg");words.add("max.muster@mail.com");
		ArrayList<Float> confidences = new ArrayList<>();
		confidences.add(70f);confidences.add(40f);confidences.add(70f);confidences.add(20f);confidences.add(90f);confidences.add(40f);confidences.add(80f);
		AnalysisResult result = new AnalysisResult(null,words , null, confidences, 0, null, null, null);
		Map<String,LabeledWord> ner = new HashMap<>();
		ner.put("FN", fn);
		ner.put("LN", ln);
		ner.put("ST", street);
		ner.put("PLZ", plz);
		ner.put("ORT", ort);
		ner.put("EMA", ema);

		Map<String, BusinessCardField> fields = p.process(result, ner);
		for(BusinessCardField f : fields.values()) {
			System.out.println(f.getNERLabel()+" "+f.getField()+" isWrong:"+f.isWrong()+" isUnsure:"+f.isUnsure());
		}
	}

}
