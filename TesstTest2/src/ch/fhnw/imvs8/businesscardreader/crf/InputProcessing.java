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

public class InputProcessing {

	// static String toCRF = "C:\\Documents\\Software\\CRF++-0.58";
	// static String toTestCRF = "C:\\Documents\\Dropbox\\ip6-doc\\CRF";
	static String toCRF = "/home/olry/Documents/Software/CRF++-0.58";
	static String toTestCRF = "/home/olry/Documents/Software/Dropbox/ip6-doc/CRF";

	public static void main(String[] args) throws IOException {
		readOutput(toTestCRF + "/clean_test.data");
	}

	public static void process(String[] args) throws IOException {
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

	public static BufferedReader readOutput(String toTestData) throws IOException {
		// Process process = new ProcessBuilder(toCRF + "\\crf_learn", toCRF +
		// "\\example\\chunking\\template", toCRF
		// + "\\example\\chunking\\train.data", toCRF +
		// "\\example\\chunking\\model").start();

		Process process = new ProcessBuilder(toCRF + "/crf_test", "-v2", "-m", toTestCRF + "/model2", toTestData).start();

		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		return new BufferedReader(isr);

		/*
		 * BufferedReader br = new BufferedReader(isr); String line;
		 * 
		 * while ((line = br.readLine()) != null) { String[] lineArray =
		 * line.split("\t"); if (lineArray.length > 2) { for (int i = 18; i <
		 * lineArray.length; i++) { System.out.print(lineArray[i] + " "); } }
		 * System.out.println(); }
		 */
	}

	public static void countFuckingWords() throws IOException {
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

	public static void replaceTestData() throws IOException {
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
	}
}
