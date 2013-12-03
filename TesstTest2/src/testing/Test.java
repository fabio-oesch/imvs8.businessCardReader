package testing;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import ch.fhnw.imvs8.businesscardreader.imagefilters.AutoBinaryFilter;
import ch.fhnw.imvs8.businesscardreader.imagefilters.GenericFilterBundle;
import ch.fhnw.imvs8.businesscardreader.imagefilters.GrayScaleFilter;
import ch.fhnw.imvs8.businesscardreader.ocr.AnalysisResult;
import ch.fhnw.imvs8.businesscardreader.ocr.OCREngine;

public class Test {

	// path to place where business cards are
	final static File folder = new File("C:\\Users\\Jon\\FHNW\\IP5\\testdata\\business-cards");
	// path to place where logs are stored
	final static String logs = "C:\\Users\\Jon\\FHNW\\IP5\\testdata\\Logs\\";
	// average errors/Mail adresse
	static double errorsPerMail = 0;

	public static void main(String[] args) throws IOException {
		testXMLS();
		// testImageDisplay();
	}

	/**
	 * creates a new file which writes the texts which it reads from the picture
	 * into a new picture with the ending test.png
	 * 
	 * @throws IOException
	 */
	public static void testImageDisplay() throws IOException {
		GenericFilterBundle filters = new GenericFilterBundle();
		filters.appendFilter(new GrayScaleFilter());
		OCREngine engine = new OCREngine(filters);

		String[] folderList = folder.list();
		for (int folders = 0; folders < folderList.length; folders++) {
			File solutionFolder = new File(folder.getAbsolutePath() + "/" + folderList[folders] + "/testimages/");

			AnalysisResult analysisResult = null;
			PictureDisplayTest pictureDisplay = null;
			File[] solutionFolderList = solutionFolder.listFiles();
			for (int file = 0; solutionFolderList != null && file < solutionFolderList.length; file++) {
				if (!solutionFolderList[file].getAbsolutePath().contains("test.")) {

					analysisResult = engine.analyzeImage(solutionFolderList[file]);
					pictureDisplay = new PictureDisplayTest(solutionFolderList[file]);
					for (int word = 0; word < analysisResult.getResultSize(); word++) {
						pictureDisplay.addText(new Color((int) ((100 - analysisResult.getConfidence(word)) * 2.5), 0, 0), analysisResult.getBoundingBox(word).height,
								analysisResult.getBoundingBox(word).x, analysisResult.getBoundingBox(word).y, analysisResult.getWord(word));
					}
					pictureDisplay.finish(solutionFolderList[file].getAbsolutePath().substring(0, solutionFolderList[file].getAbsolutePath().lastIndexOf('.')) + "test"
							+ solutionFolderList[file].getAbsolutePath().substring(solutionFolderList[file].getAbsolutePath().lastIndexOf('.')));
				}
			}

		}
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
	private static void testXMLForName(OCREngine engine, String name) throws IOException {
		File solutionFolder = new File(folder.getAbsolutePath() + "/" + name + "/solution/");

		File testFolder = new File(folder.getAbsolutePath() + "/" + name + "/testimages/");

		// Get xml File from Scanner
		File scannerFile = null;
		File[] solutionFolderList = solutionFolder.listFiles();
		int index = 0;
		while (!solutionFolderList[index++].getAbsolutePath().contains(".xml")) {
		}
		scannerFile = solutionFolderList[index - 1];

		// Write into log file
		File logFile = new File(logs + name + "_logs.txt");
		if (!logFile.exists()) {
			logFile.createNewFile();
		}
		FileWriter fw = new FileWriter(logFile.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);

		double errorsPerCard = 0;
		double percentagePerMail = 0;

		// Compare with every file in folder
		File[] testFolderList = testFolder.listFiles();
		for (int file = 0; file < testFolderList.length; file++) {
			XMLTest test = new XMLTest(scannerFile, engine.analyzeImage(testFolderList[file]), bw);

			if (file == 0) {
				bw.write("# of pictures: " + testFolderList.length + "\n");
			}

			errorsPerCard += test.getErrors();
			percentagePerMail += test.getPercentageErrors();

			bw.write(name + "_" + testFolderList[file].getName());

			bw.write("Picturename: " + testFolderList[file].getName() + "\n");
			bw.write("Average # of errors: " + test.getPercentageErrors() + "\n");
		}
		errorsPerMail += percentagePerMail / testFolderList.length;
		bw.write("Total # of errors: " + errorsPerCard);
		bw.close();
	}

	/**
	 * test all the xml files in a folder
	 * 
	 * @throws IOException
	 *             needs permission to write into a file and create it
	 */
	public static void testXMLS() throws IOException {

		// Add filters to the engine
		GenericFilterBundle filters = new GenericFilterBundle();
		filters.appendFilter(new GrayScaleFilter());
		filters.appendFilter(new AutoBinaryFilter());
		OCREngine engine = new OCREngine(filters);

		// test for a specific name
		// testXMLForName(engine, "franco.dalmolin@collanos.com");

		// tests all the files in the folder
		String[] folderList = folder.list();
		for (int folders = 0; folders < folderList.length; folders++) {
			testXMLForName(engine, folderList[folders]);
		}

		// logs for the entire folder
		File logFile = new File(logs + "_logs.txt");
		if (!logFile.exists()) {
			logFile.createNewFile();
		}
		FileWriter fw = new FileWriter(logFile.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write("Average Percentage Errors per Mail: " + errorsPerMail / folderList.length + "\n");
		bw.close();
	}
}
