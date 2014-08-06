package ch.fhnw.imvs8.businesscardreader.postprocessing;

import java.util.Map;

import ch.fhnw.imvs8.businesscardreader.BusinessCardField;
import ch.fhnw.imvs8.businesscardreader.ner.LabeledWord;
import ch.fhnw.imvs8.businesscardreader.ocr.OcrResult;

/**
 * The implementing classes are responsible for doing the postprocessing the output of the NER Engine.
 * 
 * It should:
 * 		correct errors in the words
 * 		identify fields which contain errors that the postprocessor cannot fix.
 * 		identify fields which might contain errors
 * 		translate the NER Engine output to a useful output to the user.
 * 
 * @author jon
 *
 */
public interface PostProcessor {

	/**
	 * This method takes the input from both OCR and NER Engine and tries to identify and correct errrors.
	 * 
	 * @param ocrResult Data from the OCR Engine
	 * @param nerResult Data from the NER Engine
	 * @return Map containing human readable Labels as Key and the businesscard fields as value.
	 */
	public Map<String,BusinessCardField> process(OcrResult ocrResult,Map<String,LabeledWord> nerResult);
}
