package ch.fhnw.imvs8.businesscardreader.testingframework;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import ch.fhnw.imvs8.businesscardreader.imagefilters.GenericFilterProcessor;
import ch.fhnw.imvs8.businesscardreader.imagefilters.GrayScaleFilter;
import ch.fhnw.imvs8.businesscardreader.imagefilters.LaplaceSharpenFilter;
import ch.fhnw.imvs8.businesscardreader.imagefilters.Phansalkar;
import ch.fhnw.imvs8.businesscardreader.ner.LabeledWord;
import ch.fhnw.imvs8.businesscardreader.ocr.AnalysisResult;
import ch.fhnw.imvs8.businesscardreader.ocr.OCREngine;
import ch.fhnw.imvs8.businesscardreader.testingframework.crf.createtestdata.ModelGenerator;
import ch.fhnw.imvs8.businesscardreader.testingframework.ocr.diff_match_patch;
import ch.fhnw.imvs8.businesscardreader.testingframework.ocr.diff_match_patch.Diff;
import ch.fhnw.imvs8.businesscardreader.testingframework.ocr.diff_match_patch.Operation;

public class ocrAndCrfTest {

	private static OCREngine engine;
	private static HashMap<String, String> xmlAtts;
	private static String toCRF = "/usr/local/bin";
	private static String toSVN = "/home/olry/Documents/School/Project/businesscardreader/";
	private static String toModel = "testdata/CRF/crfModels";
	private static String toLogs = "/testdata/CRF/crfLogs";

	public static void main(String[] args) {
		GenericFilterProcessor filters = new GenericFilterProcessor();
		filters.appendFilter(new GrayScaleFilter());
		filters.appendFilter(new LaplaceSharpenFilter());
		filters.appendFilter(new Phansalkar());

		engine = new OCREngine(filters);
		readBusinessCards();
	}

	public static void readBusinessCards() {
		String[] folderList = new File(toSVN + "testdata/business-cards/").list();
		int i = 0;
		while (!folderList[i++].equals("edgar.gmuer@noser.com")) {
			String[] solutionfiles = new File(toSVN + "testdata/business-cards/" + folderList[i] + "/cardscan_raw/").list();
			int j = 0;
			while (!solutionfiles[j++].endsWith(".xml"))
				;
			File xmlFile = new File(toSVN + "testdata/business-cards/" + folderList[i] + "/cardscan_raw/" + solutionfiles[j - 1]);
			HashMap<String, String> solution = readScannerXML(xmlFile);

			String[] testFiles = new File(toSVN + "testdata/business-cards/" + folderList[i] + "/testimages/").list();
			for (String fileName : testFiles)
				if (!fileName.contains("debug") || !fileName.contains("scale"))
					testPicture(fileName, folderList[i], solution);
		}
	}

	private static void testPicture(String fileName, String folderName, HashMap<String, String> solution) {
		try {
			AnalysisResult result = engine.analyzeImage(new File(toSVN + "testdata/business-cards/" + folderName + "/testimages/" + fileName));
			File tmp = new File("tmp/testfile");
			tmp.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(tmp));
			for (int i = 0; i < result.getResultSize(); i++)
				writer.append(result.getWord(i) + "\n");
			writer.close();

			ModelGenerator.createFile("tmp/testfile", "tmp/testFeaturesSet");
			readOutput("tmp/testFeaturesSet", "crossval0.txtModelNewF");

			tmp.delete();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void readOutput(String toTestData, String modelName) throws IOException, InterruptedException {
		Process process = new ProcessBuilder(toCRF + "/crf_test", "-v1", "-m", toSVN + toModel + "/" + modelName, toTestData).start();
		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);

		BufferedWriter incorrectWriter = new BufferedWriter(new FileWriter(new File(toSVN + toLogs + "/pipeline " + modelName)));
		System.out.println(toSVN + toLogs + "/pipeline " + modelName);
		String correctWord;
		String line;
		diff_match_patch diffEngine = new diff_match_patch();
		int inserted = 0;
		int deleted = 0;
		int correct = 0;
		int position = 0;
		while ((line = br.readLine()) != null)
			if (!line.startsWith("#"))
				if (line.length() < 2) {
					inserted = 0;
					deleted = 0;
					correct = 0;
					incorrectWriter.append("\n");
				} else {
					String[] lineArray = line.split("\t");
					if (lineArray.length > 2) {
						// only works when -v1 or -v2 is set
						String labelAndConfidence = lineArray[lineArray.length - 1];
						int dashIndex = labelAndConfidence.indexOf('/');

						String label = labelAndConfidence.substring(0, dashIndex);
						double conf = Double.parseDouble(labelAndConfidence.substring(dashIndex + 1));
						LabeledWord res = new LabeledWord(label, lineArray[0], conf, position++);

						correctWord = inHashMap(res.getLabel());

						if (correctWord != null) {
							List<Diff> differences = diffEngine.diff_main(correctWord, res.getWordAsString());
							for (Diff d : differences)
								if (d.operation == Operation.DELETE)
									// deleted = false negative
									deleted += d.text.length();
								else if (d.operation == Operation.INSERT)
									// inserted = false positive
									inserted += d.text.length();
								else
									correct += d.text.length();
							// System.out.println(d.text + "changed");
							double precision = correct / (double) (correct + inserted);
							double recall = correct / (double) (correct + deleted);
							double fmeasure = 2 * precision * recall / (precision + recall);
							incorrectWriter.append(correctWord + "\t" + res.getWordAsString() + "\t" + res.getLabel() + "\t" + precision + "\t" + recall + "\t"
									+ fmeasure + "\n");
						} else
							incorrectWriter.append("label not found\t" + res.getWordAsString() + "\t" + res.getLabel() + "\n");

					}
				}
		incorrectWriter.close();

	}

	public static String inHashMap(String label) {
		if (label.equals("FN"))
			return xmlAtts.get("First Name");
		else if (label.equals("LN"))
			return xmlAtts.get("Last Name");
		else if (label.equals("ST"))
			return xmlAtts.get("Street Address");
		else if (label.equals("PLZ"))
			return xmlAtts.get("Postal Code");
		else if (label.equals("ORT"))
			return xmlAtts.get("City");
		else if (label.equals("I-TN"))
			return xmlAtts.get("Phone");
		else if (label.equals("I-FN"))
			return xmlAtts.get("Phone.Fax");
		else if (label.equals("I-MN"))
			return xmlAtts.get("Phone.Mobile");
		else if (label.equals("EMA"))
			return xmlAtts.get("E-mail");
		/*
		 * else if (label.equals("WEB")) return xmlAtts.get("");
		 */
		else if (label.equals("ORG"))
			return xmlAtts.get("Company");
		else if (label.equals("TIT"))
			return xmlAtts.get("Title");
		return null;
	}

	/**
	 * reads the XML file from the scanner and creates a new scanner attribute.
	 * After all the scanner attributes have been scanned we are trying to
	 * calculate the scale factor and the offset of the tesseract picture to the
	 * scanner picture.
	 * 
	 * @param xmlInputFile
	 *            A file with the location to the xml file
	 * @param analysisResult
	 *            Result of the tesseract output
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	public static HashMap<String, String> readScannerXML(File xmlInputFile) {

		xmlAtts = new HashMap<>();

		// Create a XMLInputFactory which will read the xml-taggs
		XMLInputFactory inputFactor = XMLInputFactory.newInstance();
		try {
			// Create a reader with the ANSI Encoding
			XMLStreamReader reader = inputFactor.createXMLStreamReader(new InputStreamReader(new FileInputStream(xmlInputFile), "ISO-8859-1"));
			// Save text and fieldName in a String
			String text = null;
			String fieldName = null;

			// goes through every element
			while (reader.hasNext()) {
				// only need to check if the current element is a StartingTag
				if (reader.isStartElement())
					if (reader.getLocalName() == "list")
						fieldName = reader.getAttributeValue(0);
					else if (reader.getLocalName() == "label")
						xmlAtts.put(fieldName, reader.getAttributeValue(0));
				reader.next();
			}
		} catch (FileNotFoundException e) {
			System.out.println(xmlInputFile.getAbsolutePath());
			e.printStackTrace();
		} catch (XMLStreamException e) {
			System.out.println(xmlInputFile.getAbsolutePath());
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return xmlAtts;
	}

}
