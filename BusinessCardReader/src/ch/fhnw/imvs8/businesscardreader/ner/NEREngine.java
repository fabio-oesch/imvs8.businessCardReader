package ch.fhnw.imvs8.businesscardreader.ner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import ch.fhnw.imvs8.businesscardreader.ocr.OcrResult;
import ch.fhnw.imvs8.businesscardreader.testingframework.crf.createtestdata.ModelGenerator;

/**
 * Represents a Named Entity Recognition Engine. It takes the Input of the
 * OCREngine and runs them through CRF++.
 * 
 * @author jon
 */
public class NEREngine {
	private final String tmpFileName = "test.data";

	private FeatureCreator creator;
	private final String toCRF;
	private final String toModel;
	private final String tmpFileLoc;
	private final HashSet<String> concatenationRequired;

	/**
	 * Generates
	 * 
	 * @param modelFile
	 *            location to the training files for the NEREngine
	 * @param creator
	 *            Lookup Tables to use
	 * @throws FileNotFoundException
	 */
	public NEREngine(String CRFLocation, String modelFile, FeatureCreator creator) throws FileNotFoundException {
		File f = new File(modelFile);
		if (!f.exists())
			throw new FileNotFoundException("model file not found: " + modelFile);

		toCRF = CRFLocation;
		toModel = modelFile;
		this.creator = creator;

		concatenationRequired = new HashSet<>();
		concatenationRequired.add("I-TF");
		concatenationRequired.add("I-TW");
		concatenationRequired.add("I-TM");
		concatenationRequired.add("ORG");

		final String dir = System.getProperty("java.io.tmpdir");
		tmpFileLoc = dir + File.separator + tmpFileName;
	}

	/**
	 * Analyses a Result of an OCR Run
	 * 
	 * @param results
	 *            of the OCREngine
	 * @return Named Entities. Table with the NamedEntities, the Label (for
	 *         example "email") is the Key and the NamedEntity the value;
	 */
	public Map<String, LabeledWord> analyse(OcrResult results) {
		Map<String, LabeledWord> answer = null;
		try {
			creator.createFeatures(results, tmpFileLoc);
			List<LabeledWord> crfResult = readOutput(tmpFileLoc, results.getResultSize());
			answer = concatenateEntitiesSimple(crfResult);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return answer;
	}

	/**
	 * Calls CRF over the command line and stores the results in a list
	 * 
	 * @param toTestData
	 *            file location with a list of words and their features.
	 * @param size
	 *            how many words are here to recognize
	 * @return List of Named Entities. This is the Raw CRF Output.
	 * @throws IOException
	 */
	private List<LabeledWord> readOutput(String toTestData, int size) throws IOException {
		Process process = new ProcessBuilder(toCRF + "/crf_test", "-v1", "-m", toModel, toTestData).start();
		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);

		int positionIndex = 0;
		List<LabeledWord> tokens = new ArrayList<>(size);
		String line;
		while ((line = br.readLine()) != null) {
			String[] lineArray = line.split("\t");
			if (lineArray.length > 2) {
				// only works when -v1 and -v2 is not set
				String labelAndConfidence = lineArray[lineArray.length - 1];
				int dashIndex = labelAndConfidence.lastIndexOf('/');

				String label = labelAndConfidence.substring(0, dashIndex);
				double conf = Double.parseDouble(labelAndConfidence.substring(dashIndex + 1));
				LabeledWord res = new LabeledWord(label, lineArray[0], conf, positionIndex++);

				tokens.add(res);
			}
		}

		return tokens;
	}

	/**
	 * Simply concatenate entities with the same tag.
	 * 
	 * @param entities
	 * @return concatenated entities in a Map, the Key is the tag.
	 */
	private Map<String, LabeledWord> concatenateEntitiesSimple(List<LabeledWord> entities) {
		HashMap<String, LabeledWord> answer = new HashMap<>(entities.size());

		for (LabeledWord e : entities)
			if (answer.containsKey(e.getLabel())) {
				LabeledWord entity = answer.get(e.getLabel());
				entity.addWordAfter(e);
			} else
				answer.put(e.getLabel(), e);

		return answer;
	}
}
