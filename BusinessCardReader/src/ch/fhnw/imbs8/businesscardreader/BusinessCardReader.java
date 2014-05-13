package ch.fhnw.imbs8.businesscardreader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import ch.fhnw.imvs8.businesscardreader.crf.LookupTables;
import ch.fhnw.imvs8.businesscardreader.crf.NEREngine;
import ch.fhnw.imvs8.businesscardreader.crf.NamedEntity;
import ch.fhnw.imvs8.businesscardreader.imagefilters.GenericFilterProcessor;
import ch.fhnw.imvs8.businesscardreader.imagefilters.GrayScaleFilter;
import ch.fhnw.imvs8.businesscardreader.imagefilters.Phansalkar;
import ch.fhnw.imvs8.businesscardreader.ocr.AnalysisResult;
import ch.fhnw.imvs8.businesscardreader.ocr.OCREngine;

public class BusinessCardReader {

	private static final String LOOKUP_TABLES_FOLDER = "";
	private static final String NER_CONFIGURATION_FOLDER = "";
	private final OCREngine ocr;
	private final NEREngine ner;

	public BusinessCardReader() throws Exception {
		GenericFilterProcessor filters = new GenericFilterProcessor();
		filters.appendFilter(new GrayScaleFilter());
		filters.appendFilter(new Phansalkar());

		ocr = new OCREngine(filters);

		LookupTables tables = null;
		try {
			tables = new LookupTables(LOOKUP_TABLES_FOLDER);
		} catch (Exception e) {
			final String currentDir = System.getProperty("user.dir");
			String message = "Invalid File or Folder at: " + currentDir + File.separator + LOOKUP_TABLES_FOLDER;
			Exception ex = new Exception(message, e);
			e.printStackTrace();
			throw ex;
		}
		ner = new NEREngine(NER_CONFIGURATION_FOLDER, tables);
	}

	/**
	 * reads an image and returns a list
	 * 
	 * @param image
	 *            path to image
	 * @return Named entities found in this picture
	 * @throws FileNotFoundException
	 */
	public Map<String, NamedEntity> readImage(String image) throws FileNotFoundException {
		AnalysisResult ocrAnalsysis = ocr.analyzeImage(new File(image));

		return ner.analyse(ocrAnalsysis);
	}
}
