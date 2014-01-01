package testing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import testing.diff_match_patch.Diff;
import testing.diff_match_patch.Operation;
import ch.fhnw.imvs8.businesscardreader.ocr.AnalysisResult;
import ch.fhnw.imvs8.businesscardreader.ocr.OCREngine;

public class TesseractTest {

	static String testdataFolder;

	public static void main(String args[]) throws IOException {
		testdataFolder = "C:\\Users\\Jon\\FHNW\\IP5\\testdata\\tesseract-testdata\\";

		String solution = null;
		File testImagesFolder = new File(testdataFolder + "testimages");
		File solutionFile = new File(testdataFolder + "solutionText.txt");

		FileWriter fileStream = new FileWriter(testdataFolder + "log.csv");

		HashMap<Character, Integer> statistics = new HashMap<>();
		BufferedWriter out = new BufferedWriter(fileStream);
		BufferedReader r = new BufferedReader(new FileReader(solutionFile));
		solution = r.readLine();
		System.out.println(solution);
		OCREngine engine = new OCREngine();
		diff_match_patch diffEngine = new diff_match_patch();

		out.append("Filename;precision;recall;f-measure;inserted String;deleted String\n");
		//test
		for (File f : testImagesFolder.listFiles()) {
			int inserted = 0;
			int deleted = 0;
			int correct = 0;
			StringBuilder insertedStr = new StringBuilder();
			StringBuilder deletedStr = new StringBuilder();

			String result = buildResultString(engine.analyzeImage(f));
			System.out.println(result);
			List<Diff> differences = diffEngine.diff_main(solution, result);
			for (Diff d : differences) {
				if (d.operation == Operation.DELETE) {
					//deleted = false negative
					deleted += d.text.length();
					deletedStr.append(d.text);
					deletedStr.append(" | ");

					for (char c : d.text.toCharArray()) {
						if (!statistics.containsKey(c))
							statistics.put(c, 1);

						Integer count = statistics.get(c);
						statistics.remove(c);
						statistics.put(c, count + 1);
					}
				} else if (d.operation == Operation.INSERT) {
					//inserted = false positive
					inserted += d.text.length();
					insertedStr.append(d.text);
					insertedStr.append(" | ");
				} else {
					correct += d.text.length();
					//System.out.println(d.text + "changed");
				}
			}
			double precision = correct / (double) (correct + inserted);
			double recall = correct / (double) (correct + deleted);
			double fmeasure = 2 * precision * recall / (precision + recall);
			out.append(f.getName() + " ;");
			out.append(String.format("%.3f", precision) + " ;");
			out.append(String.format("%.3f", recall) + " ;");
			out.append(String.format("%.3f", fmeasure) + " ;");
			out.append(insertedStr + " ;");
			out.append(deletedStr + " \n");

			//
		}

		out.close();
		writeStatisticsFile(testdataFolder + "log_statistics.csv", statistics);

	}

	private static void writeStatisticsFile(String file, HashMap<Character, Integer> statistics) {
		Iterator<Character> it = statistics.keySet().iterator();
		FileWriter w = null;
		try {
			w = new FileWriter(file);
			while (it.hasNext()) {
				Character c = it.next();
				Integer i = statistics.get(c);
				w.write(c + ";" + i + "\n");
			}
		} catch (Exception e) {

		} finally {
			try {
				w.close();
			} catch (Exception e) {
			}
		}
	}

	private static String buildResultString(AnalysisResult res) {
		StringBuilder answer = new StringBuilder();
		for (int i = 0; i < res.getResultSize(); i++) {
			answer.append(res.getWord(i));
			answer.append(" ");

		}
		answer.deleteCharAt(answer.length() - 1); //delete last space
		return answer.toString();
	}
}
