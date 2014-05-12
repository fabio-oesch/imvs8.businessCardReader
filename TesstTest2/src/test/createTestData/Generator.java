package test.createTestData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import ch.fhnw.imvs8.businesscardreader.imagefilters.Preprocessor;
import ch.fhnw.imvs8.businesscardreader.ocr.OCREngine;

public class Generator {
	String testFiles;
	File folder;

	public void main(String[] args) {
		boolean schwambi = false;
		if (schwambi) {
			folder = new File("C:\\Users\\Jon\\FHNW\\IP5\\testdata\\business-cards");
			testFiles = "C:\\Users\\Jon\\FHNW\\IP5\\testdata\\crf-testdata\\";
		} else {
			folder = new File("/Documents/School/Project/testdata/business-cards");
			testFiles = "/Documents/School/Project/testdata/crf-testdata/";
		}
	}

	void createTestFiles(Preprocessor filters) {

		OCREngine engine = new OCREngine(filters);

		// logs for the entire folder
		File logFile = new File(testFiles + logName);
		if (!logFile.exists()) {
			logFile.createNewFile();
		}
		FileWriter fw = new FileWriter(logFile.getAbsoluteFile());
		BufferedWriter bwLog = new BufferedWriter(fw);
		bwLog.write("E-Mail;PictureID;Precision;Recall;F_Measure;BoundingBox Precision;BoundingBox Recall;BoundingBox F_Measure; Character Precision; Character Recall; Character F_Measure \n");

		// tests all the files in the folder
		String[] folderList = folder.list();
		// for (int folders = 0; folders < folderList.length; folders++) {
		// testXMLForName(engine, folderList[folders], bwLog);
		// }

		bwLog.close();
	}
}
