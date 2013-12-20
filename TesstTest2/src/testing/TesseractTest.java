package testing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import testing.diff_match_patch.Diff;
import testing.diff_match_patch.Operation;
import ch.fhnw.imvs8.businesscardreader.ocr.AnalysisResult;
import ch.fhnw.imvs8.businesscardreader.ocr.OCREngine;

public class TesseractTest {

	static File testImagesFolder;
	static File testFile;
	static File logFile;

	public static void main(String args[]) throws IOException {
		String solution = null;
		testImagesFolder = new File("C:\\Users\\Jon\\FHNW\\IP5\\testdata\\tesseract-testdata\\testimages");
		testFile = new File("C:\\Users\\Jon\\FHNW\\IP5\\testdata\\tesseract-testdata\\solutionText.txt");

		BufferedWriter out = new BufferedWriter(new FileWriter("C:\\Users\\Jon\\FHNW\\IP5\\testdata\\tesseract-testdata\\log.csv"));

		BufferedReader r = new BufferedReader(new FileReader(testFile));
		solution = r.readLine();

		OCREngine engine = new OCREngine();
		diff_match_patch diffEngine = new diff_match_patch();

		out.append("Filename;#inserted characters;#deleted characters;inserted String;deleted String\n");
		//test
		for (File f : testImagesFolder.listFiles()) {
			int inserted = 0;
			int deleted = 0;
			StringBuilder insertedStr = new StringBuilder();
			StringBuilder deletedStr = new StringBuilder();

			String result = buildResultString(engine.analyzeImage(f));

			List<Diff> differences = diffEngine.diff_main(solution, result);
			for (Diff d : differences) {
				if (d.operation == Operation.DELETE) {
					//deleted = false negative
					deleted += d.text.length();
					deletedStr.append(d.text);
					deletedStr.append(" | ");

				} else if (d.operation == Operation.INSERT) {
					//inserted, changed = false positive
					inserted += d.text.length();
					insertedStr.append(d.text);
					insertedStr.append(" | ");
				}
			}
			out.append(f.getName() + " ;");
			out.append(inserted + " ;");
			out.append(deleted + " ;");
			out.append(insertedStr + " ;");
			out.append(deletedStr + " \n");

			//
		}

		out.close();
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
