package ch.fhnw.imvs8.businesscardreader.crf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.fhnw.imvs8.businesscardreader.ocr.AnalysisResult;

/**
 * Represents a Named Entity Recognition Engine. It takes the Input of the OCREngine and runs
 * them through CRF++.
 * 
 * @author jon
 * 
 */
public class NEREngine {

	private FeatureCreator creator;
	private final String toCRF = "/home/olry/Documents/Software/CRF++-0.58";
	private final String toTestCRF;
	private final String tmpFileLoc = "/clean_test.data";
	private final HashSet<String> concatenationRequired;
	/**
	 * Generates
	 * 
	 * @param trainingFiles
	 *            location to the training files for the NEREngine
	 * @param tables
	 *            Lookup Tables to use
	 */
	public NEREngine(String trainingFiles, LookupTables tables) {
		this.toTestCRF = trainingFiles;
		this.creator = new FeatureCreator(tables);
		
		this.concatenationRequired = new HashSet<>();
		this.concatenationRequired.add("I-TF");
		this.concatenationRequired.add("I-TW");
		this.concatenationRequired.add("I-TM");
		this.concatenationRequired.add("ORG");
	}

	/**
	 * Analyses a Result of an OCR Run
	 * 
	 * @param results
	 *            of the OCREngine
	 * @return Named Entities.
	 */
	public Map<String, NamedEntity> analyse(AnalysisResult results) {
		Map<String, NamedEntity> answer = null;
		try {
			creator.createFeatures(results, tmpFileLoc);
			List<NamedEntity> crfResult = this.readOutput(tmpFileLoc, results.getResultSize());
			answer = this.concatenateEntities(crfResult, this.concatenationRequired);
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
		// Process process = new ProcessBuilder(toCRF + "\\crf_learn", toCRF +
		// "\\example\\chunking\\template", toCRF
		// + "\\example\\chunking\\train.data", toCRF +
		// "\\example\\chunking\\model").start();

		// Process process = new ProcessBuilder(toCRF + "/crf_test", "-v1",
		// "-m", toTestCRF + "/model2", toTestData).start();
		Process process = new ProcessBuilder(toCRF + "/crf_test", "-m", toTestCRF + "/model2", toTestData).start();

		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);

		List<NamedEntity> tokens = new ArrayList<>(size);

		String line;
		while ((line = br.readLine()) != null) {
			String[] lineArray = line.split("\t");
			if (lineArray.length > 2) {
				// only works when -v1 and -v2 is not set
				double conf = Double.parseDouble(lineArray[lineArray.length - 2]);
				NamedEntity res = new NamedEntity(lineArray[lineArray.length - 1],lineArray[0],conf);
				
				tokens.add(res);
			}
		}

		return tokens;
	}

	/**
	 * Entities like telephone numbers are split over serveral words. For
	 * example 079 333 33 33 are separate words, this method concatenates them into one entity.
	 * 
	 * @param entities
	 * @param needsConcatenation
	 * 			Set of Strings, each string defines a Tag which needs concatenation with the following entities in the sequence "entities".
	 * 			For example: TEL
	 * @return
	 */
	private Map<String, NamedEntity> concatenateEntities(List<NamedEntity> entities,Set<String> needsConcatenation) {
		HashMap<String, NamedEntity> answer = new HashMap<>(entities.size());
		
		//state machine, concatenate is the only state switcher
		boolean concatenate = false;
		NamedEntity entity = null;
		String concatenation = null;
		for(NamedEntity e : entities) {
			if(!concatenate) {
				//if they don't need to be concatenated, put them to answers
				if(needsConcatenation.contains(e.tag)) {
					entity = e;
					concatenation = e.entity;
					concatenate = true;
				} else {
					answer.put(e.tag, e);
				}
			}
			else {
				//while entities have the same tag, concatenate them
				if(entity.tag.equals(e.tag)) {
					concatenation += " " + e.entity;
				} else {
					NamedEntity conc = new NamedEntity(entity.tag,concatenation,e.confidence);
					answer.put(conc.tag, conc);
					concatenation = null;
					concatenate = false;
					entity = null;
				}
			}
		}
		return answer;
	}

	private void countFuckingWords() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(toTestCRF + "/clean_test.txt"));
		String line = reader.readLine();
		String[] lineArr = line.split(" ");
		int should = lineArr.length;
		int lineNumber = 1;
		while (line != null) {
			lineNumber++;
			line = reader.readLine();
			lineArr = line.split(" ");
			if (should != lineArr.length)
				System.out.println(lineNumber + " " + lineArr.length);
		}
	}

	private void replaceTestData() throws IOException {
		BufferedReader readerTrainData = new BufferedReader(new FileReader(toTestCRF + "/clean_template_train.data"));
		BufferedReader readerTestData = new BufferedReader(new FileReader(toTestCRF + "/train.data"));
		BufferedWriter writerTestData = new BufferedWriter(new FileWriter(toTestCRF + "/clean_train.data"));

		HashMap<String, String[]> hashmap = new HashMap<>();
		String line = readerTrainData.readLine();
		String[] lineArr = line.split(" ");
		while (line != null) {
			lineArr = line.split(" ");
			hashmap.put(lineArr[lineArr.length - 1], lineArr);
			line = readerTrainData.readLine();
		}

		line = readerTestData.readLine();
		lineArr = line.split(" ");
		while (line != null) {
			lineArr = line.split(" ");
			if (lineArr.length > 1) {
				writerTestData.write(lineArr[0] + " ");
				String[] lineResult = hashmap.get(lineArr[lineArr.length - 1]);
				if (lineResult == null) {
					lineResult = hashmap.get(lineArr[lineArr.length - 1].substring(2));
					if (lineResult == null) {
						lineResult = hashmap.get("IDK");
					}
				}
				for (int i = 0; i < lineResult.length; i++) {
					writerTestData.write(lineResult[i] + " ");
				}
			}
			writerTestData.write("\n");
			line = readerTestData.readLine();
		}

		writerTestData.close();
		readerTestData.close();
		readerTrainData.close();
	}
}
