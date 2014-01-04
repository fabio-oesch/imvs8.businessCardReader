package testing;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import ch.fhnw.imvs8.businesscardreader.imagefilters.AutoBinaryFilter;
import ch.fhnw.imvs8.businesscardreader.imagefilters.Bernsen;
import ch.fhnw.imvs8.businesscardreader.imagefilters.FilterBundle;
import ch.fhnw.imvs8.businesscardreader.imagefilters.GenericFilterBundle;
import ch.fhnw.imvs8.businesscardreader.imagefilters.GrayScaleFilter;
import ch.fhnw.imvs8.businesscardreader.imagefilters.LightFilter;
import ch.fhnw.imvs8.businesscardreader.imagefilters.Otsu;
import ch.fhnw.imvs8.businesscardreader.imagefilters.Phansalkar;
import ch.fhnw.imvs8.businesscardreader.imagefilters.Sauvola;
import ch.fhnw.imvs8.businesscardreader.ocr.AnalysisResult;
import ch.fhnw.imvs8.businesscardreader.ocr.OCREngine;

public class Test {

	// path to place where business cards are
	static File folder;
	// path to place where logs are stored
	static String logs;
	// average errors/Mail adresse
	static double errorsPerMail = 0;
	static boolean generateDebugImages = true;

	public static void main(String[] args) throws IOException {
		boolean schwambi = true;
		if (schwambi) {
			folder = new File("C:\\Users\\Jon\\FHNW\\IP5\\testdata\\business-cards");
			logs = "C:\\Users\\Jon\\FHNW\\IP5\\testdata\\Logs\\";
		} else {
			folder = new File("/School/Projekt/testdata/business-cards");
			logs = "/School/Projekt/testdata/Logs/";
		}

		// Add filters to the engine
		GenericFilterBundle filters = new GenericFilterBundle();
		filters.appendFilter(new GrayScaleFilter());
		//filters.appendFilter(new LightFilter());
		filters.appendFilter(new AutoBinaryFilter());
		//filters.appendFilter(new Phansalkar());
		//filters.appendFilter(new CloseFilter());

		testXMLS("_logs.csv", filters);
		//testImageDisplay(filters);

		//testAllConfigurations();
	}

	/**
	 * creates a new file which writes the texts which it reads from the picture
	 * into a new picture with the ending test.png creates a new file which
	 * writes the texts which it reads from the picture into a new picture with
	 * the ending test.png
	 * 
	 * @throws IOException
	 */
	public static void testImageDisplay(GenericFilterBundle filters) throws IOException {
		OCREngine engine = new OCREngine(filters);

		String[] folderList = folder.list();
		for (int folders = 0; folders < folderList.length; folders++) {
			File solutionFolder = new File(folder.getAbsolutePath() + "/" + folderList[folders] + "/testimages/");

			AnalysisResult analysisResult = null;
			PictureDisplayTest pictureDisplay = null;
			File[] solutionFolderList = solutionFolder.listFiles();
			for (int file = 0; solutionFolderList != null && file < solutionFolderList.length; file++) {

				if (!solutionFolderList[file].getAbsolutePath().contains("debug") || !solutionFolderList[file].getAbsolutePath().endsWith(".txt")) {
					System.out.println(solutionFolderList[file]);
					analysisResult = engine.analyzeImage(solutionFolderList[file]);
					pictureDisplay = new PictureDisplayTest(solutionFolderList[file]);
					for (int word = 0; word < analysisResult.getResultSize(); word++) {
						pictureDisplay.addText(new Color((int) ((100 - analysisResult.getConfidence(word)) * 2.55), 0, 0), analysisResult.getBoundingBox(word).height,
								analysisResult.getBoundingBox(word), analysisResult.getWord(word));
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
	private static void testXMLForName(OCREngine engine, String name, BufferedWriter bwLog) throws IOException {
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
			if (!testFolderList[file].getAbsolutePath().contains("debug") && !testFolderList[file].getAbsolutePath().contains("_scale.txt")) {
				System.out.println(testFolderList[file].getAbsolutePath());
				AnalysisResult analysisResult = engine.analyzeImage(testFolderList[file]);

				if (file == 0) {
					bw.write("# of pictures: " + testFolderList.length + "\n");
				}

				XMLTest test = new XMLTest(scannerFile, testFolderList[file], engine.analyzeImage(testFolderList[file]), bw);

				errorsPerCard += test.getErrors();
				percentagePerMail += test.getPercentageErrors();
				String logline = name + ";" + testFolderList[file].getName() + ";" + String.format("%.3f", test.getPrecision()) + ";" + String.format("%.3f", test.getRecall())
						+ ";" + String.format("%.3f", test.f_Measure()) + ";" + String.format("%.3f", test.getPercentageErrors()) + ";" + test.uniqueStuff() + "\n";
				bwLog.write(logline);
				bw.write(logline);

				// write really cool debug picture
				if (generateDebugImages) {
					PictureDisplayTest pictureDisplay = new PictureDisplayTest(new File(testFolderList[file].getAbsolutePath() + "_debug.png"));
					for (int word = 0; word < analysisResult.getResultSize(); word++) {
						pictureDisplay.addText(new Color((int) ((100 - analysisResult.getConfidence(word)) * 2.55), 0, 0), analysisResult.getBoundingBox(word).height,
								analysisResult.getBoundingBox(word), analysisResult.getWord(word));
					}
					pictureDisplay.finish(testFolderList[file].getAbsolutePath() + "_debug_tesseract.png");
				}
			}
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
	public static void testXMLS(String logName, FilterBundle filters) throws IOException {
		OCREngine engine = new OCREngine(filters);

		if (generateDebugImages)
			engine.enableDebugMode();

		// logs for the entire folder
		File logFile = new File(logs + logName);
		if (!logFile.exists()) {
			logFile.createNewFile();
		}
		FileWriter fw = new FileWriter(logFile.getAbsoluteFile());
		BufferedWriter bwLog = new BufferedWriter(fw);
		bwLog.write("E-Mail;PictureID;Precision;Recall;F_Measure;Average Errors per Picture;unique_attributes;Scale;X Offset;Y Offset \n");

		testXMLForName(engine, "alban.frei@zuehlke.com", bwLog);

		// tests all the files in the folder

		String[] folderList = folder.list();
		for (int folders = 0; folders < folderList.length; folders++) {
			testXMLForName(engine, folderList[folders], bwLog);
		}

		// bwLog.write("Average Percentage Errors per Mail: "
		// + String.format("%.3f", errorsPerMail / folderList.length) + "\n");
		bwLog.close();
	}

	public static final void testAllConfigurations() {
		String subF = "AllConfigurations\\";
		ArrayList<GenericFilterBundle> bundles = new ArrayList<>();
		ArrayList<String> logFiles = new ArrayList<>();

		GenericFilterBundle b;

		//testnothing
		bundles.add(null);
		logFiles.add(subF + "NoPreprocessing");

		//test grayscale
		b = new GenericFilterBundle();
		b.appendFilter(new GrayScaleFilter());
		bundles.add(b);
		logFiles.add(subF + "GrayScaleOnly");

		//test autothresholdbinarizer
		b = new GenericFilterBundle();
		b.appendFilter(new GrayScaleFilter());
		b.appendFilter(new AutoBinaryFilter());
		bundles.add(b);
		logFiles.add(subF + "autothresholdbinarizer");

		//test autothresholdbinarizer with lightfilter
		b = new GenericFilterBundle();
		b.appendFilter(new GrayScaleFilter());
		b.appendFilter(new LightFilter());
		b.appendFilter(new AutoBinaryFilter());
		bundles.add(b);
		logFiles.add(subF + "AutothresholdBinarizerWithLightfilter");

		//test AdaptiveBinarizerSauvola
		b = new GenericFilterBundle();
		b.appendFilter(new GrayScaleFilter());
		b.appendFilter(new Sauvola());
		bundles.add(b);
		logFiles.add(subF + "AdaptiveBinarizerSauvola");

		//test AdaptiveBinarizerOtsu
		b = new GenericFilterBundle();
		b.appendFilter(new GrayScaleFilter());
		b.appendFilter(new Otsu());
		bundles.add(b);
		logFiles.add(subF + "AdaptiveBinarizerOtsu");

		//test AdaptiveBinarizerBernsen
		b = new GenericFilterBundle();
		b.appendFilter(new GrayScaleFilter());
		b.appendFilter(new Bernsen());
		bundles.add(b);
		logFiles.add(subF + "AdaptiveBinarizerBernsen");

		//test AdaptiveBinarizerPhansalkar
		b = new GenericFilterBundle();
		b.appendFilter(new GrayScaleFilter());
		b.appendFilter(new Phansalkar());
		bundles.add(b);
		logFiles.add(subF + "AdaptiveBinarizerPhansalkar");

		for (int i = 0; i < bundles.size(); i++) {
			try {
				GenericFilterBundle bundle = bundles.get(i);
				String logFile = logFiles.get(i);
				testXMLS(logFile, bundle);

				if (bundle != null) {
					FileWriter w = new FileWriter(logs + logFile + "_stats.txt");
					w.write("TimeUsed: " + bundle.getUsedTimeMilis() + "\n");
					w.write("PicturesProcessed: " + bundle.getFilteredPictureCount());
					w.close();
				}
				//put out
			} catch (Exception e) {
				/* ignore */
				e.printStackTrace();
			}
		}
	}
}
