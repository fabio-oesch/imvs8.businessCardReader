package testing;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import ch.fhnw.imvs8.businesscardreader.imagefilters.GenericFilterBundle;
import ch.fhnw.imvs8.businesscardreader.imagefilters.GrayScaleFilter;
import ch.fhnw.imvs8.businesscardreader.ocr.AnalysisResult;
import ch.fhnw.imvs8.businesscardreader.ocr.OCREngine;

public class Test {

	final static File folder = new File(
			"/School/Projekt/testdata/business-cards");

	public static void main(String[] args) throws IOException {
		// testXMLS();
		testImageDisplay();
	}

	// File scannerImage = new File(
	// "/School/Projekt/testdata/business-cards/christophe.meili@jaree.com/solution/scan_2013-10-02_12-15-55-image-preprocessed.png");
	//
	// File scannerFile = new File(
	// "/School/Projekt/testdata/business-cards/christophe.meili@jaree.com/solution/scan_2013-10-02_12-15-55.xml");
	//
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
			for (int file = 0; file < solutionFolderList.length; file++) {
				analysisResult = engine.analyzeImage(solutionFolderList[file]);
				pictureDisplay = new PictureDisplayTest(
						solutionFolderList[file]);
				for (int i = 0; i < analysisResult.getResultSize(); i++) {
					pictureDisplay.addText(
							new Color((int) ((100 - analysisResult
									.getConfidence(i)) * 2.5), 0, 0),
							analysisResult.getBoundingBox(i).x, analysisResult
									.getBoundingBox(i).y, analysisResult
									.getWord(i));
				}
				pictureDisplay.finish(solutionFolderList[file]
						.getAbsolutePath().substring(
								0,
								solutionFolderList[file].getAbsolutePath()
										.lastIndexOf('.'))
						+ "test"
						+ solutionFolderList[file].getAbsolutePath().substring(
								solutionFolderList[file].getAbsolutePath()
										.lastIndexOf('.')));
			}

		}
	}

	public static void testXMLS() throws FileNotFoundException {

		GenericFilterBundle filters = new GenericFilterBundle();
		filters.appendFilter(new GrayScaleFilter());
		OCREngine engine = new OCREngine(filters);

		String[] folderList = folder.list();
		for (int folders = 0; folders < folderList.length; folders++) {
			File solutionFolder = new File(folder.getAbsolutePath() + "/"
					+ folderList[folders] + "/solution/");

			AnalysisResult analysisResult = null;
			File scannerFile = null;
			File[] solutionFolderList = solutionFolder.listFiles();
			for (int file = 0; file < solutionFolderList.length; file++) {
				if (solutionFolderList[file].getAbsolutePath().contains(
						"preprocessed.png")) {
					analysisResult = engine
							.analyzeImage(solutionFolderList[file]);
				}
				if (solutionFolderList[file].getAbsolutePath().contains(".xml")) {
					scannerFile = solutionFolderList[file];
				}
			}

			XMLTest test = new XMLTest(scannerFile, analysisResult,
					folderList[folders]);
		}
	}
}
