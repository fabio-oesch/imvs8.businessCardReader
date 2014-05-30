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
import ch.fhnw.imvs8.businesscardreader.ocr.AnalysisResult;

/**
 * Represents a Named Entity Recognition Engine. It takes the Input of the OCREngine and runs
 * them through CRF++.
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
		if(!f.exists())
			throw new FileNotFoundException("model file not found: "+modelFile);
		
		this.toCRF = CRFLocation;
		this.toModel = modelFile;
		this.creator = creator;
		
		this.concatenationRequired = new HashSet<>();
		this.concatenationRequired.add("I-TF");
		this.concatenationRequired.add("I-TW");
		this.concatenationRequired.add("I-TM");
		this.concatenationRequired.add("ORG");
		
		final String dir = System.getProperty("java.io.tmpdir");
		tmpFileLoc = dir +File.separator+tmpFileName;
	}

	/**
	 * Analyses a Result of an OCR Run
	 * 
	 * @param results
	 *            of the OCREngine
	 * @return Named Entities.
	 * 			  Table with the NamedEntities, the Label (for example "email") is the Key and the NamedEntity the value;
	 */
	public Map<String, NamedEntity> analyse(AnalysisResult results) {
		Map<String, NamedEntity> answer = null;
		try {
			creator.createFeatures(results, tmpFileLoc);
			List<NamedEntity> crfResult = this.readOutput(tmpFileLoc, results.getResultSize());
			answer = this.concatenateEntitiesSimple(crfResult);
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
	private List<NamedEntity> readOutput(String toTestData, int size) throws IOException {
		Process process = new ProcessBuilder(toCRF + "/crf_test", "-v1", "-m", toModel + "/model", toTestData).start();
		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		
		List<NamedEntity> tokens = new ArrayList<>(size);
		String line;
		while ((line = br.readLine()) != null) {
			String[] lineArray = line.split("\t");
			if (lineArray.length > 2) {
				// only works when -v1 and -v2 is not set
				String labelAndConfidence = lineArray[lineArray.length - 1];
				int dashIndex = labelAndConfidence.indexOf('/');
				
				String label = labelAndConfidence.substring(0, dashIndex);
				double conf = Double.parseDouble(labelAndConfidence.substring(dashIndex+1));
				NamedEntity res = new NamedEntity(label,lineArray[0],conf);
				
				tokens.add(res);
			}
		}

		return tokens;
	}

	/**
	 * Simply concatenate entities with the same tag.
	 * @param entities
	 * @return concatenated entities in a Map, the Key is the tag.
	 */
	private Map<String, NamedEntity> concatenateEntitiesSimple(List<NamedEntity> entities) {
		HashMap<String, NamedEntity> answer = new HashMap<>(entities.size());
		
		for(NamedEntity e : entities) {
			if(answer.containsKey(e.getLabel())) {
				NamedEntity entity = answer.get(e.getLabel());
				answer.remove(e.getLabel());
				NamedEntity concatenated = new NamedEntity(e.getLabel(),entity.getEntity()+" "+e.getEntity(),Math.min(entity.getConfidence(),e.getConfidence()));
				answer.put(e.getLabel(), concatenated);
			} else {
				answer.put(e.getLabel(), e);
			}
		}
		
		return answer;
	}
}
