package ch.fhnw.imvs8.businesscardreader.postprocessing;

import java.util.Map;

import ch.fhnw.imvs8.businesscardreader.BusinessCardField;
import ch.fhnw.imvs8.businesscardreader.ner.LabeledWord;
import ch.fhnw.imvs8.businesscardreader.ocr.AnalysisResult;

public interface Processor {

	public Map<String,BusinessCardField> process(AnalysisResult ocrResult,Map<String,LabeledWord> nerResult);
}
