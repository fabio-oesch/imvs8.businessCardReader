package testing;

import java.io.File;
import java.io.FileNotFoundException;

import ch.fhnw.imvs8.businesscardreader.imagefilters.GenericFilterBundle;
import ch.fhnw.imvs8.businesscardreader.imagefilters.GrayScaleFilter;
import ch.fhnw.imvs8.businesscardreader.ocr.AnalysisResult;
import ch.fhnw.imvs8.businesscardreader.ocr.OCREngine;

public class Test {

	final static File folder = new File(
			"/School/Projekt/testdata/business-cards");

	public static void main(String[] args) throws FileNotFoundException {

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

		// File scannerImage = new File(
		// "/School/Projekt/testdata/business-cards/christophe.meili@jaree.com/solution/scan_2013-10-02_12-15-55-image-preprocessed.png");
		//
		// File scannerFile = new File(
		// "/School/Projekt/testdata/business-cards/christophe.meili@jaree.com/solution/scan_2013-10-02_12-15-55.xml");
		//
	}
}
