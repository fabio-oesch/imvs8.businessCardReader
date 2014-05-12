package ch.fhnw.imvs8.businesscardreader.crf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ch.fhnw.imvs8.businesscardreader.ocr.AnalysisResult;

/**
 * Named Entity Recognition Engine
 * Takes the Input of the OCREngine and runs them through CRF++
 * 
 * @author jon
 *
 */
public class NEREngine {

	private FeatureCreator creator;
	private final String toCRF = "/home/olry/Documents/Software/CRF++-0.58";
	private final String toTestCRF;
	private final String tmpFileLoc = "/clean_test.data";
	/**
	 * Generates
	 * @param trainingFiles location to the training files for the NEREngine
	 * @param tables Lookup Tables to use
	 */
	public NEREngine(String trainingFiles,LookupTables tables) {
		this.toTestCRF = trainingFiles;
		this.creator = new FeatureCreator(tables);
	}
	
	/**
	 * Analyses a Result of an OCR Run
	 * @param results of the OCREngine
	 * @return Named Entity Recognition
	 */
	public Map<String,String> analyse(AnalysisResult results) {
		Map<String,String> answer = null;
		try {
			creator.createFeatures(results, tmpFileLoc);
			answer = this.readOutput(tmpFileLoc);
		} 
		catch(IOException e) {
			e.printStackTrace();
		}
		
		return answer;
	}
	
	private void process(String[] args) throws IOException {
		// Process process = new ProcessBuilder(toCRF + "\\crf_learn", toCRF +
		// "\\example\\chunking\\template", toCRF
		// + "\\example\\chunking\\train.data", toCRF +
		// "\\example\\chunking\\model").start();

		Process process = new ProcessBuilder(toCRF + "/crf_learn", toTestCRF + "/template", toTestCRF + "/train.data", toTestCRF + "/model").start();

		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;

		System.out.printf("Output of running %s is:", Arrays.toString(args));

		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
	}

	private HashMap<String, String> readOutput(String toTestData) throws IOException {
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

		HashMap<String, String> tokens = new HashMap<>();
		String line;
		while ((line = br.readLine()) != null) {
			String[] lineArray = line.split("\t");
			if (lineArray.length > 2) {
				// only works when -v1 and -v2 is not set
				tokens.put(lineArray[lineArray.length - 1], lineArray[0]);
			}
		}

		return tokens;

		/*
		 * BufferedReader br = new BufferedReader(isr); String line;
		 * 
		 * while ((line = br.readLine()) != null) { String[] lineArray =
		 * line.split("\t"); if (lineArray.length > 2) { for (int i = 18; i <
		 * lineArray.length; i++) { System.out.print(lineArray[i] + " "); } }
		 * System.out.println(); }
		 */
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
