package ch.fhnw.imvs8.businesscardreader.testingframework.crf.createtestdata;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import ch.fhnw.imvs8.businesscardreader.ner.FeatureCreator;
import ch.fhnw.imvs8.businesscardreader.ner.LabeledWord;
import ch.fhnw.imvs8.businesscardreader.ner.LookupTables;
import ch.fhnw.imvs8.businesscardreader.ner.stemming.GermanStemming;
import ch.fhnw.imvs8.businesscardreader.testingframework.crf.CRFLogGenerator;

public class ModelGenerator {
	private static final int csvColumns = 6;
	private static CRFLogGenerator logs;
	private static String toCRF = "/usr/local/bin";
	private static String toModel = "/testdata/CRF/crfModels";
	private static String toLogs = "/testdata/CRF/crfLogs";
	private static String modelPref = "ModelWithConfidence";
	private static String tmpFiles = "tmp";
	private static boolean schwambi = true;
	private static boolean isTest = true;

	private static String toSVN = schwambi ? "/home/jon/dev/fuckingsvn/svn" : "/home/olry/Documents/School/Project/businesscardreader";

	public static void main(String[] args) throws Exception {
		if (isTest)
			crossValidate();
		else {
			File[] files = new File(toSVN + "/testdata/CRF/crf-testdata/crossValConfidence").listFiles();
			File temp = new File(toSVN + "/testdata/CRF/crf-testdata/temp.txt");
			if (temp.exists())
				temp.delete();
			try {
				IOCopier.joinFiles(temp, files);

				createModel(temp.getAbsolutePath(), tmpFiles + "/training", toSVN + "/testdata/CRF/configfiles/template", "allFilesModel");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// if (schwambi)
		// //
		// createModel("/home/jon/dev/fuckingsvn/svn/testdata/crf-testdata/testdata.crf.v4_c1.csv","training","/home/jon/dev/fuckingsvn/svn/testdata/crf-testdata/template");
		// testModel("/home/jon/dev/fuckingsvn/svn/testdata/crf-testdata/testdata.c1.test.csv",
		// toModel);
		// else
		// crossValidate();
		// createModel("/home/olry/Documents/School/Project/svn/testdata/crf-testdata/testdata.crf.v4_c1.csv",
		// "training",
		// "/home/olry/Documents/School/Project/svn/doc/ip6/CRF/template");
		// testModel("/home/olry/Documents/School/Project/svn/testdata/crf-testdata/testdata.c1.test.csv",
		// toModel);
	}

	public static void crossValidate() {
		File[] files = new File(toSVN + "/testdata/CRF/crf-testdata/crossValConfidence").listFiles();
		try {
			logs = new CRFLogGenerator(LabeledWord.LABELS);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Arrays.sort(files);
		
		for (File file : files) {

			// does not create a new file
			File temp = new File(toSVN + "/testdata/CRF/crf-testdata/temp.txt");
			if (temp.exists())
				temp.delete();
			File[] removedFile = new File[files.length - 1];
			boolean found = false;
			for (int i = 0; i < files.length; i++)
				if (file.equals(files[i]))
					found = true;
				else
					removedFile[found ? i - 1 : i] = files[i];
			try {
				IOCopier.joinFiles(temp, removedFile);

				String modelName = file.getName() + modelPref;
				createModel(temp.getAbsolutePath(), tmpFiles + "/training", toSVN + "/testdata/CRF/configfiles/template", modelName);
				testModel(file.getAbsolutePath(), toModel, modelName);
			} catch (Exception e) {
				e.printStackTrace();
			}

			temp.delete();
		}
	}

	public static void createModel(String inFile, String trainingFile, String templateFile, String modelName) throws Exception {
		createFile(inFile, trainingFile);
		// Create
		Process process = new ProcessBuilder(toCRF + "/crf_learn", templateFile, trainingFile, toSVN + toModel + "/" + modelName).start();
		System.out.println("Waiting for learing proccess");
		process.waitFor();
		System.out.println("done");
	}

	public static void countFuckingWords(int lineNumber, String line) {
		String[] lineArr = line.split(" ");
		int should = 34;

		lineArr = line.split(" ");
		if (should != lineArr.length) {
			System.out.println(lineNumber + " " + lineArr.length);
			System.out.println(line);
		}

	}

	public static void createFile(String inFile, String testFile) throws Exception {
		int lineNumber = 1;
		
		LookupTables table = new LookupTables("lookup_tables");
		FeatureCreator creator = new FeatureCreator(table, new GermanStemming());

		BufferedReader reader = new BufferedReader(new FileReader(inFile));
		BufferedWriter out = new BufferedWriter(new FileWriter(testFile));

		String line = null;
		ArrayList<String> lines = new ArrayList<>(100);
		while ((line = reader.readLine()) != null)
			if (line.equals(",,,,,")) {
				
				//terribly inefficient string operations, but nobody cares about the performance of this code passage
				for(int i = 0; i < lines.size();i++) {
					String[] data = new String[csvColumns];
					String l = lines.get(i);
					try{
					for(int j = csvColumns-1;j > 0;j--) {
						int lastCol = l.lastIndexOf(',');
						data[j] = (l.substring(lastCol+1)).trim();
						l = l.substring(0, lastCol);
					}
					data[0] = l;
					
					String features = creator.createLine(data[0], Integer.parseInt(data[1]), lines.size(),  Integer.parseInt(data[2]),  Integer.parseInt(data[3]),  Double.parseDouble(data[4]));
					
					String output = features.trim() + " " + data[5]+"\n";
					countFuckingWords(lineNumber,output);
					out.write(output);
					}
					catch(Exception e) {
						System.out.println(l);
						e.printStackTrace();
					}
					
				}
				lines.clear();
				out.write("\n");
				
				
			}else if (!line.equals("")) {
				lines.add(line);
				lineNumber++;
			}

		reader.close();
		out.close();

	}

	public static void testModel(String testFile, String toModel, String modelName) throws Exception {
		createFile(testFile, tmpFiles + "/testdata");
		readOutput(tmpFiles + "/testdata", modelName);
	}

	private static void readOutput(String toTestData, String modelName) throws IOException, InterruptedException {
		Process process = new ProcessBuilder(toCRF + "/crf_test", "-v1", "-m", toSVN + toModel + "/" + modelName, toTestData).start();
		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		logs = new CRFLogGenerator(LabeledWord.LABELS);

		BufferedWriter incorrectWriter = new BufferedWriter(new FileWriter(new File(toSVN + toLogs + "/incorrect " + modelName)));
		System.out.println(toSVN + toLogs + "/incorrect " + modelName);
		boolean isCorrect;
		String line;
		int mistakes = 0;
		int position = 0;
		while ((line = br.readLine()) != null)
			if (!line.startsWith("#"))
				if (line.length() < 2) {
					logs.addCard();
					incorrectWriter.append("Mistakes :" + mistakes);
					incorrectWriter.append("\n\n");
					mistakes = 0;
				} else {
					String[] lineArray = line.split("\t");
					if (lineArray.length > 2) {
						// only works when -v1 or -v2 is set
						String labelAndConfidence = lineArray[lineArray.length - 1];
						int dashIndex = labelAndConfidence.lastIndexOf('/');

						String label = labelAndConfidence.substring(0, dashIndex);
						double conf = Double.parseDouble(labelAndConfidence.substring(dashIndex + 1));
						LabeledWord res = new LabeledWord(label, lineArray[0], conf, position++);

						isCorrect = logs.addToLogs(lineArray[lineArray.length - 2], res.getLabel(), res.getConfidence());

						if (!isCorrect) {
							mistakes++;
							StringBuilder builder = new StringBuilder();
							for (int i = 0; i < lineArray.length; i++) {
								if (i == 0) {
									builder.append(lineArray[i] + "\t\t");
									i++;
								}
								builder.append(lineArray[i] + " ");
							}
							incorrectWriter.append(builder + "\n");
						}
					}
				}
		incorrectWriter.close();

		BufferedWriter writer = new BufferedWriter(new FileWriter(toSVN + toLogs + "/Testresult of the model " + modelName));
		System.out.println("Percentage all correct: " + logs.getHadAllLabelsPerCardCorrect());

		writer.write("Percentage all correct: " + logs.getHadAllLabelsPerCardCorrect() + "\n");

		String[] labels = LabeledWord.LABELS;
		writer.append("\nFMeasure per Label \n");
		double[] stuff = logs.getFMeasurePerLabel(writer, labels);
		// writer.append("\nFMeasure per Label \n");
		// for (int i = 0; i < labels.length; i++)
		// writer.append(labels[i] + " " + stuff[i] + "\n");
		// System.out.println(labels[i] + " " + stuff[i]);
		writer.close();

	}
}

class IOCopier {
	public static void joinFiles(File destination, File[] sources) throws IOException {
		OutputStream output = null;
		try {
			output = createAppendableStream(destination);
			for (File source : sources)
				appendFile(output, source);
		} finally {
			IOUtils.closeQuietly(output);
		}
	}

	private static BufferedOutputStream createAppendableStream(File destination) throws FileNotFoundException {
		return new BufferedOutputStream(new FileOutputStream(destination, true));
	}

	private static void appendFile(OutputStream output, File source) throws IOException {
		InputStream input = null;
		try {
			input = new BufferedInputStream(new FileInputStream(source));
			IOUtils.copy(input, output);
		} finally {
			IOUtils.closeQuietly(input);
		}
	}
}

class IOUtils {
	private static final int BUFFER_SIZE = 1024 * 4;

	public static long copy(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	public static void closeQuietly(Closeable output) {
		try {
			if (output != null)
				output.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
