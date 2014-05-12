package test.createTestData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import ch.fhnw.imvs8.businesscardreader.imagefilters.GenericFilterProcessor;
import ch.fhnw.imvs8.businesscardreader.imagefilters.GrayScaleFilter;
import ch.fhnw.imvs8.businesscardreader.imagefilters.LaplaceSharpenFilter;
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
		filters.appendFilter(new LaplaceSharpenFilter());
		filters.appendFilter(new Phansalkar());

		createTestFiles(filters, "testdata.crf.csv");
	}

	static void createTestFiles(Preprocessor filters, String crfName) throws IOException {

		OCREngine engine = new OCREngine(filters);

		// logs for the entire folder
		File logFile = new File(testFiles + crfName);
		if (!logFile.exists()) {
			logFile.createNewFile();
		}
		FileWriter fw = new FileWriter(logFile.getAbsoluteFile());
		BufferedWriter bwCRF = new BufferedWriter(fw);
		bwCRF.write("E-Mail;PictureID;Precision;Recall;F_Measure;BoundingBox Precision;BoundingBox Recall;BoundingBox F_Measure; Character Precision; Character Recall; Character F_Measure \n");

		// tests all the files in the folder
		String[] folderList = folder.list();
		// for (int folders = 0; folders < folderList.length; folders++) {
		// testXMLForName(engine, folderList[folders], bwLog);
		// }

		tesseractAttributes(engine, folderList[0], bwCRF);

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
	private static void tesseractAttributes(OCREngine engine, String name, BufferedWriter bwLog) throws IOException {
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
		for (int file = 0; file < testFolderList.length && file < 1; file++) {
			if (!testFolderList[file].getAbsolutePath().contains("debug") && !testFolderList[file].getAbsolutePath().contains("_scale.txt")) {
				System.out.println(testFolderList[file].getAbsolutePath());
				AnalysisResult analysisResult = engine.analyzeImage(testFolderList[file]);

				for (int i = 0; i < analysisResult.getResultSize(); i++) {
					System.out.println(analysisResult.getWord(i));
				}

				// String logline = name + ";" + testFolderList[file].getName()
				// + ";" + String.format("%.3f", test.getPrecision()) + ";" +
				// String.format("%.3f", test.getRecall())
				// + ";" + String.format("%.3f", test.f_Measure()) + ";" +
				// String.format("%.3f", test.boundingboxGetPrecision()) + ";"
				// + String.format("%.3f", test.boundingboxGetRecall()) + ";" +
				// String.format("%.3f", test.boundingboxF_Measure()) + ";"
				// + String.format("%.3f", test.characterGetPrecision()) + ";" +
				// String.format("%.3f", test.characterGetRecall()) + ";"
				// + String.format("%.3f", test.characterF_Measure()) + ";" +
				// "\n";
				// bwLog.write(logline);
			}
		}
	}
}
