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
		fail("Not yet implemented");
		/*
		 * 	public static void main(String[] args) {
		StandardProcessor p = new StandardProcessor();
		
		LabeledWord fn = new LabeledWord("FN","Max",90.0,0);
		LabeledWord ema = new LabeledWord("EMA","bla@max.bla",90.0,1);
		
		ArrayList<String> words = new ArrayList<String>();
		words.add("Max");words.add("bla@max.bla");
		ArrayList<Float> confidences = new ArrayList<>();
		confidences.add(70f);confidences.add(40f);
		AnalysisResult result = new AnalysisResult(null,words , null, confidences, 0, null, null, null);
		Map<String,LabeledWord> ner = new HashMap<>();
		ner.put("FN", fn);
		ner.put("EMA", ema);
		
		Map<String, BusinessCardField> fields = p.process(result, ner);
		for(BusinessCardField f : fields.values()) {
			System.out.println(f.getNERLabel()+" "+f.getField()+" isWrong:"+f.isWrong()+" isUnsure:"+f.isUnsure());
		}
		
	}
		 */
	}

}
