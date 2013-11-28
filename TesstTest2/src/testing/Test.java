package testing;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import ch.fhnw.imvs8.businesscardreader.imagefilters.AutoBinaryFilter;
import ch.fhnw.imvs8.businesscardreader.imagefilters.GenericFilterBundle;
import ch.fhnw.imvs8.businesscardreader.imagefilters.GrayScaleFilter;
import ch.fhnw.imvs8.businesscardreader.ocr.AnalysisResult;
import ch.fhnw.imvs8.businesscardreader.ocr.OCREngine;

public class Test {

	final static File folder = new File(
			"/School/Projekt/testdata/business-cards");

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
			File solutionFolder = new File(folder.getAbsolutePath() + "/"
					+ folderList[folders] + "/testimages/");

			AnalysisResult analysisResult = null;
			PictureDisplayTest pictureDisplay = null;
			File[] solutionFolderList = solutionFolder.listFiles();
			for (int file = 0; solutionFolderList != null
					&& file < solutionFolderList.length; file++) {
				if (!solutionFolderList[file].getAbsolutePath().contains(
						"test.")) {

					analysisResult = engine
							.analyzeImage(solutionFolderList[file]);
					pictureDisplay = new PictureDisplayTest(
							solutionFolderList[file]);
					for (int word = 0; word < analysisResult.getResultSize(); word++) {
						pictureDisplay.addText(
								new Color((int) ((100 - analysisResult
										.getConfidence(word)) * 2.5), 0, 0),
								analysisResult.getBoundingBox(word).height,
								analysisResult.getBoundingBox(word).x,
								analysisResult.getBoundingBox(word).y,
								analysisResult.getWord(word));
					}
					pictureDisplay.finish(solutionFolderList[file]
							.getAbsolutePath().substring(
									0,
									solutionFolderList[file].getAbsolutePath()
											.lastIndexOf('.'))
							+ "test"
							+ solutionFolderList[file].getAbsolutePath()
									.substring(
											solutionFolderList[file]
													.getAbsolutePath()
													.lastIndexOf('.')));
				}
			}

		}
	}

	private static void testXMLForName(OCREngine engine, String name)
			throws FileNotFoundException {
		File solutionFolder = new File(folder.getAbsolutePath() + "/" + name
				+ "/solution/");

		File testFolder = new File(folder.getAbsolutePath() + "/" + name
				+ "/testimages/");

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
			XMLTest test = new XMLTest(scannerFile,
					engine.analyzeImage(testFolderList[file]), name);

		}
	}

	public static void testXMLS() throws FileNotFoundException {

		GenericFilterBundle filters = new GenericFilterBundle();
		filters.appendFilter(new GrayScaleFilter());
		filters.appendFilter(new AutoBinaryFilter());
		OCREngine engine = new OCREngine(filters);

		testXMLForName(engine, "bernhard.schmidt@a-design.ch");

		// String[] folderList = folder.list();
		// for (int folders = 0; folders < folderList.length; folders++) {
		// testXMLForName(engine, folderList[folders]);
		// }
	}
}
