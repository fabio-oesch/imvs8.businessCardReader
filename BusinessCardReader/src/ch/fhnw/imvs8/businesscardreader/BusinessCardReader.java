package ch.fhnw.imvs8.businesscardreader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.fhnw.imvs8.businesscardreader.imagefilters.GenericFilterProcessor;
import ch.fhnw.imvs8.businesscardreader.imagefilters.GrayScaleFilter;
import ch.fhnw.imvs8.businesscardreader.imagefilters.Phansalkar;
import ch.fhnw.imvs8.businesscardreader.ner.FeatureCreator;
import ch.fhnw.imvs8.businesscardreader.ner.LookupTables;
import ch.fhnw.imvs8.businesscardreader.ner.NEREngine;
import ch.fhnw.imvs8.businesscardreader.ner.LabeledWord;
import ch.fhnw.imvs8.businesscardreader.ner.stemming.GermanStemming;
import ch.fhnw.imvs8.businesscardreader.ocr.AnalysisResult;
import ch.fhnw.imvs8.businesscardreader.ocr.OCREngine;
import ch.fhnw.imvs8.businesscardreader.postprocessing.Processor;
import ch.fhnw.imvs8.businesscardreader.postprocessing.StandardProcessor;
import ch.fhnw.imvs8.businesscardreader.vcard.VCardCreator;

/**
 * Class which combines everything. It is responsible for reading an image and
 * returning the tagged entities found in it.
 * 
 * It also handles the configuration of the subsystems and throws an exception,
 * if one of them is corrupt.
 * 
 * @author Jon
 * 
 */
public class BusinessCardReader {

	private static final String LOOKUP_TABLES_FOLDER = "lookup_tables";
	private static final String NER_CONFIGURATION_FOLDER = "crfdata";
	private static final String CRF_LOCATION = "/usr/local/bin";
	private final OCREngine ocr;
	private final NEREngine ner;
	private final Processor proc;

	/**
	 * Creates a Business Card Reader Object
	 * 
	 * @param dataFolder
	 *            path to folder containing all configuration files. Subfolders
	 *            Expected: -crfdata -lookup_tables
	 * 
	 *            It makes sense to put the folder "tessdata" in there with the
	 *            others, but it doesn't have to be. Be sure to set the system
	 *            environment variable "TESSDATA_PREFIX" to the parent folder of
	 *            "tessdata".
	 * @throws Exception
	 *             Throws expection if the dataFolder does not contain the right
	 *             subfolders and files or if a configuration file is corrupt.
	 */
	public BusinessCardReader(String dataFolder) throws Exception {
		GenericFilterProcessor filters = new GenericFilterProcessor();
		filters.appendFilter(new GrayScaleFilter());
		filters.appendFilter(new Phansalkar());

		ocr = new OCREngine(filters);

		LookupTables tables = new LookupTables(dataFolder + File.separator + LOOKUP_TABLES_FOLDER);
		FeatureCreator creator = new FeatureCreator(tables, new GermanStemming());
		
		ner = new NEREngine(CRF_LOCATION, dataFolder + File.separator + NER_CONFIGURATION_FOLDER +File.separator + "model", creator);
		
		proc = new StandardProcessor();
	}

	/**
	 * reads an image and returns a map of found words.
	 * The keys are tags found for this entity and the values are the entities themselfes.
	 * 
	 * @param image
	 *            path to image
	 * @return Named entities found in this picture
	 * @throws FileNotFoundException
	 */
	public BusinessCard readImage(String image) throws FileNotFoundException {
		AnalysisResult ocrResult = ocr.analyzeImage(new File(image));
		Map<String, LabeledWord> nerResult = ner.analyse(ocrResult);
		Map<String, BusinessCardField> fields = proc.process(ocrResult, nerResult);
			
		return new BusinessCard(fields,ocrResult,nerResult);

	}

}
