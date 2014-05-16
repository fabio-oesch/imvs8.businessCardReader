package ch.fhnw.imvs8.businesscardreader.testingframework.crf.createtestdata;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import ch.fhnw.imvs8.businesscardreader.imagefilters.GenericFilterProcessor;
import ch.fhnw.imvs8.businesscardreader.imagefilters.GrayScaleFilter;
import ch.fhnw.imvs8.businesscardreader.imagefilters.Phansalkar;
import ch.fhnw.imvs8.businesscardreader.imagefilters.Preprocessor;
import ch.fhnw.imvs8.businesscardreader.ocr.AnalysisResult;
import ch.fhnw.imvs8.businesscardreader.ocr.OCREngine;

public class Generator {
	static String testFiles;
	static File folder;

	public static void main(String[] args) throws IOException {
		boolean schwambi = false;
		if (schwambi) {
			folder = new File("C:\\Users\\Jon\\FHNW\\IP5\\testdata\\business-cards");
			testFiles = "C:\\Users\\Jon\\FHNW\\IP5\\testdata\\crf-testdata\\";
		} else {
			folder = new File("/Documents/School/Project/testdata/business-cards");
			testFiles = "/Documents/School/Project/testdata/crf-testdata/";
		}

		// Add filters to the engine
		GenericFilterProcessor filters = new GenericFilterProcessor();
		filters.appendFilter(new GrayScaleFilter());
		filters.appendFilter(new Phansalkar());

		createTestFiles(filters, "testdata.crf.txt");
	}

	/**
	 * creates test files to make a model or test crf++
	 * 
	 * @param filters
	 *            the filters which should be applied on the pictures
	 * @param crfName
	 *            name of the file which shoulde be created
	 * @throws IOException
	 *             if cant read to file or cant write into it
	 */
	static void createTestFiles(Preprocessor filters, String crfName) throws IOException {

		OCREngine engine = new OCREngine(filters);

		// logs for the entire folder
		File logFile = new File(testFiles + crfName);
		if (!logFile.exists()) {
			logFile.createNewFile();
		}
		FileWriter fw = new FileWriter(logFile.getAbsoluteFile());
		BufferedWriter bwCRF = new BufferedWriter(fw);

		// tests all the files in the folder
		String[] folderList = folder.list();
		for (int folders = 0; folders < folderList.length; folders++) {
			tesseractAttributes(engine, folderList[folders], bwCRF);
			bwCRF.write("\n");
		}

		bwCRF.close();
	}

	/**
	 * Test an xml file and writes the logs to the logs folder
	 * 
	 * @param engine
	 *            OCREngine
	 * @param name
	 *            name of the e-mail which should be tested
	 * @throws IOException
	 *             needs permission to write and create a new file
	 */
	private static void tesseractAttributes(OCREngine engine, String name, BufferedWriter bwCRF) throws IOException {
		File solutionFolder = new File(folder.getAbsolutePath() + "/" + name + "/solution/");

		File testFolder = new File(folder.getAbsolutePath() + "/" + name + "/testimages/");

		// Get xml File from Scanner
		File scannerFile = null;
		File[] solutionFolderList = solutionFolder.listFiles();
		int index = 0;
		while (!solutionFolderList[index++].getAbsolutePath().contains(".xml")) {
		}
		scannerFile = solutionFolderList[index - 1];

		// Compare with every file in folder
		File[] testFolderList = testFolder.listFiles();
		for (int file = 0; file < testFolderList.length; file++) {
			if (!testFolderList[file].getAbsolutePath().contains("debug")
					&& !testFolderList[file].getAbsolutePath().contains("_scale.txt")
					&& testFolderList[file].getAbsolutePath().contains("scan")) {
				System.out.println(testFolderList[file].getAbsolutePath());
				AnalysisResult analysisResult = engine.analyzeImage(testFolderList[file]);

				HashMap<String, String> xmlResuls = GetXMLAttributes.readXMLAttributes(scannerFile);
				boolean found;
				for (int i = 0; i < analysisResult.getResultSize(); i++) {
					if (!analysisResult.getWord(i).equals("") && !analysisResult.getWord(i).contains(" ")) {
						found = false;
						Iterator<String> it = xmlResuls.keySet().iterator();
						while (it.hasNext() && !found) {
							String current = it.next();
							if (current.contains(analysisResult.getWord(i))) {
								bwCRF.write("=\"" + analysisResult.getWord(i) + "\"" + ";" + xmlResuls.get(current)
										+ "\n");
								found = true;
							}
						}
						if (!found) {
							addLabel(bwCRF, analysisResult.getWord(i));
						}
					}
				}
			}
		}
	}

	/**
	 * creates a label if none is found or if no case is matching leave it blank
	 * 
	 * @param bwCRF
	 *            writer of the file to write into it
	 * @param currentWord
	 *            the word which is being processed
	 * @throws IOException
	 */
	public static void addLabel(BufferedWriter bwCRF, String currentWord) throws IOException {
		if (currentWord.toLowerCase().contains("www")) {
			bwCRF.write("=\"" + currentWord + "\";web\n");
		} else if (currentWord.toLowerCase().contains("@") || currentWord.contains("mail")) {
			bwCRF.write("=\"" + currentWord + "\";E-mail\n");
		} else if (currentWord.toLowerCase().contains("tel") || currentWord.toLowerCase().contains("dire")
				|| currentWord.toLowerCase().contains("phone")) {
			bwCRF.write("=\"" + currentWord + "\";B-TN\n");
		} else if (currentWord.toLowerCase().contains("mobile")) {
			bwCRF.write("=\"" + currentWord + "\";B-MN\n");
		} else if (currentWord.toLowerCase().contains("fax")) {
			bwCRF.write("=\"" + currentWord + "\";B-FN\n");
		} else if (currentWord.toLowerCase().contains("tel")) {
			bwCRF.write("=\"" + currentWord + "\";B-TN\n");
		} else if (currentWord.toLowerCase().contains("ch-")) {
			bwCRF.write("=\"" + currentWord + "\";PLZ\n");
		} else if (currentWord.length() == 4) {
			bwCRF.write("=\"" + currentWord + "\";PLZ\n");
		} else {
			bwCRF.write("=\"" + currentWord + "\"\n");
		}
	}
}
