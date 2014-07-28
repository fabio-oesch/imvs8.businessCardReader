package ch.fhnw.imvs8.businesscardreader.testingframework;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import ch.fhnw.imvs8.businesscardreader.imagefilters.GenericFilterProcessor;
import ch.fhnw.imvs8.businesscardreader.imagefilters.GrayScaleFilter;
import ch.fhnw.imvs8.businesscardreader.imagefilters.Phansalkar;
import ch.fhnw.imvs8.businesscardreader.ner.FeatureCreator;
import ch.fhnw.imvs8.businesscardreader.ner.LabeledWord;
import ch.fhnw.imvs8.businesscardreader.ner.LookupTables;
import ch.fhnw.imvs8.businesscardreader.ner.NEREngine;
import ch.fhnw.imvs8.businesscardreader.ner.stemming.GermanStemming;
import ch.fhnw.imvs8.businesscardreader.ocr.AnalysisResult;
import ch.fhnw.imvs8.businesscardreader.ocr.OCREngine;
import ch.fhnw.imvs8.businesscardreader.testingframework.ocr.diff_match_patch;
import ch.fhnw.imvs8.businesscardreader.testingframework.ocr.diff_match_patch.Diff;
import ch.fhnw.imvs8.businesscardreader.testingframework.ocr.diff_match_patch.Operation;

public class ocrAndCrfTest {
	private static boolean testWithFailure = false;

	private static OCREngine engine;
	private static HashMap<String, String> xmlAtts;
	private static HashMap<String, Double> fMeasurePerLabel = new HashMap<>();
	private static HashMap<String, Double> precisionPerLabel = new HashMap<>();
	private static HashMap<String, Double> recallPerLabel = new HashMap<>();

	private static HashMap<String, Integer> CountPerLabel = new HashMap<>();
	private static HashMap<String, Integer> CountFMeasureOne = new HashMap<>();
	private static String[] xmlAttName = { "FN", "LN", "ST", "PLZ", "ORT", "I-TN", "I-FN", "I-MN", "EMA", "ORG", "TIT" };
	private static String[] xmlStuff = { "First Name", "Last Name", "Street Address", "Postal Code", "City", "Phone", "Fax", "Phone.Mobile", "E-mail",
			"Company", "Title" };
	private static double[] prec = new double[11];
	private static double[] reca = new double[11];
	private static double[] fmeas = new double[11];
	private static boolean[] xmlAttUsed = new boolean[11];

	private static HashMap<String, Integer> truePositive = new HashMap<>();
	private static HashMap<String, Integer> falsePositive = new HashMap<>();
	private static HashMap<String, Integer> falseNegative = new HashMap<>();

	private static String toSVN = "/home/jon/dev/fuckingsvn/svn/";

	private static String toModel = "testdata/CRF/crfModels/";
	private static String toLogs = "/testdata/CRF/pipelineLogs/";

	private static final String LOOKUP_TABLES_FOLDER = "lookup_tables";
	private static final String CRF_LOCATION = "/usr/local/bin";
	private static NEREngine ner;

	private static final String[] LAST_EMAIL_PERSON = { "a.mathur@axes-systems.com", "edgar.gmuer@noser.com", "markus.berner@scs.ch", "peter.brandt@ergon.ch",
			"simon.stamm@actelion.com", "zeno.staemmer@albistechnologies.com" };
	private static final String[] MODELS = { "crossval0.txtModelWithConfidence", "crossval1.txtModelWithConfidence", "crossval2.txtModelWithConfidence",
			"crossval3.txtModelWithConfidence", "crossval4.txtModelWithConfidence" };

	public static void main(String[] args) {
		GenericFilterProcessor filters = new GenericFilterProcessor();
		filters.appendFilter(new GrayScaleFilter());
		filters.appendFilter(new Phansalkar());

		engine = new OCREngine(filters);
		for (int i = 0; i < LAST_EMAIL_PERSON.length - 1; i++)
			readBusinessCards(LAST_EMAIL_PERSON[i], LAST_EMAIL_PERSON[i + 1], i);

		createOverallFile();
	}

	private static void createOverallFile() {
		DecimalFormat df = new DecimalFormat("#.###");
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(toSVN + toLogs + "/allFiles.txt")));
			writer.append("F-Measure Per Label:\n");
			writer.append("Label\tf-mes\tPrec\tRecall\n");
			Iterator<String> it = fMeasurePerLabel.keySet().iterator();
			while (it.hasNext()) {
				String label = it.next();
				writer.append(label + "\t" + df.format(fMeasurePerLabel.get(label) / CountPerLabel.get(label)) + "\t"
						+ df.format(precisionPerLabel.get(label) / CountPerLabel.get(label)) + "\t"
						+ df.format(recallPerLabel.get(label) / CountPerLabel.get(label)) + "\n");
			}

			writer.append("\nCount of F-Measures = 1 per label\n");
			Iterator<String> it2 = CountFMeasureOne.keySet().iterator();
			while (it2.hasNext()) {
				String label = it2.next();
				writer.append(label + "\t " + CountFMeasureOne.get(label) + " / " + CountPerLabel.get(label) + "\t" + CountFMeasureOne.get(label)
						/ (double) CountPerLabel.get(label) * 100 + "%\n");
			}

			writer.append("\nF-Measure for each Label\n");
			writer.append("Label\tf-mes\tPrec\tRecall\n");
			Iterator<String> it3 = truePositive.keySet().iterator();
			while (it3.hasNext()) {
				String label = it3.next();
				double precision = truePositive.get(label)
						/ (double) (truePositive.get(label) + (falsePositive.containsKey(label) ? falsePositive.get(label) : 0));
				double recall = truePositive.get(label)
						/ (double) (truePositive.get(label) + (falseNegative.containsKey(label) ? falseNegative.get(label) : 0));
				double fmeasure = 2 * precision * recall / (precision + recall);
				writer.append(label + "\t" + df.format(fmeasure) + "\t" + df.format(precision) + "\t" + df.format(recall) + "\n");
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void readBusinessCards(String firstEmail, String lastEmail, int index) {
		String[] folderList = new File(toSVN + "testdata/business-cards/").list();
		Arrays.sort(folderList);
		int i = 0;
		while (!folderList[i].equals(firstEmail))
			i++;
		if (index != 0)
			i++;
		while (!folderList[i++].equals(lastEmail)) {
			String[] solutionfiles = new File(toSVN + "testdata/business-cards/" + folderList[i] + "/cardscan_raw/").list();
			int j = 0;
			while (!solutionfiles[j].endsWith(".xml"))
				j++;
			File xmlFile = new File(toSVN + "testdata/business-cards/" + folderList[i] + "/cardscan_raw/" + solutionfiles[j]);
			HashMap<String, String> solution = readScannerXML(xmlFile);

			String[] testFiles = new File(toSVN + "testdata/business-cards/" + folderList[i] + "/testimages/").list();
			for (String fileName : testFiles)
				if (!(fileName.contains("debug") || fileName.contains("scale") || fileName.contains("IMAG")))
					testPicture(fileName, folderList[i], solution, index);
		}
	}

	private static void testPicture(String fileName, String folderName, HashMap<String, String> solution, int index) {
		try {
			Arrays.fill(xmlAttUsed, false);
			System.out.println(toSVN + "testdata/business-cards/" + folderName + "/testimages/" + fileName);
			AnalysisResult result = engine.analyzeImage(new File(toSVN + "testdata/business-cards/" + folderName + "/testimages/" + fileName));

			LookupTables tables = new LookupTables("." + File.separator + LOOKUP_TABLES_FOLDER);
			FeatureCreator creator = new FeatureCreator(tables, new GermanStemming());

			ner = new NEREngine(CRF_LOCATION, toSVN + toModel + MODELS[index], creator);
			Map<String, LabeledWord> pictureResult = ner.analyse(result);

			readOutput(pictureResult, fileName, folderName);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void readOutput(Map<String, LabeledWord> pictureResult, String fileName, String folderName) throws IOException, InterruptedException {

		String correctWord;
		diff_match_patch diffEngine = new diff_match_patch();
		HashMap<String, String> saveCRFOutput = new HashMap<>();

		Arrays.fill(prec, 0);
		Arrays.fill(reca, 0);
		Arrays.fill(fmeas, 0);
		Iterator<Map.Entry<String, LabeledWord>> it = pictureResult.entrySet().iterator();
		while (it.hasNext()) {
			int inserted = 0;
			int deleted = 0;
			int correct = 0;
			Map.Entry<String, LabeledWord> pairs = it.next();
			it.remove(); // avoids a ConcurrentModificationException
			correctWord = inHashMap(pairs.getKey());

			if (correctWord != null) {
				List<Diff> differences = diffEngine.diff_main(correctWord.trim(), pairs.getValue().getWordAsString().trim());
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
				if (Double.isNaN(fmeasure))
					fmeasure = 0;
				if (Double.isNaN(recall))
					recall = 0;
				if (Double.isNaN(precision))
					precision = 0;

				precisionPerLabel.put(pairs.getKey(), precisionPerLabel.containsKey(pairs.getKey()) ? precisionPerLabel.get(pairs.getKey()) + precision
						: precision);
				recallPerLabel.put(pairs.getKey(), recallPerLabel.containsKey(pairs.getKey()) ? recallPerLabel.get(pairs.getKey()) + recall : recall);
				fMeasurePerLabel.put(pairs.getKey(), fMeasurePerLabel.containsKey(pairs.getKey()) ? fMeasurePerLabel.get(pairs.getKey()) + fmeasure : fmeasure);
				CountPerLabel.put(pairs.getKey(), CountPerLabel.containsKey(pairs.getKey()) ? CountPerLabel.get(pairs.getKey()) + 1 : 1);

				if (fmeasure == 1)
					CountFMeasureOne.put(pairs.getKey(), CountFMeasureOne.containsKey(pairs.getKey()) ? CountFMeasureOne.get(pairs.getKey()) + 1 : 1);
				prec[Arrays.asList(xmlAttName).indexOf(pairs.getKey())] = precision;
				reca[Arrays.asList(xmlAttName).indexOf(pairs.getKey())] = recall;
				fmeas[Arrays.asList(xmlAttName).indexOf(pairs.getKey())] = fmeasure;

				if (precision > 0.6)
					truePositive.put(pairs.getKey(), truePositive.containsKey(pairs.getKey()) ? truePositive.get(pairs.getKey()) + 1 : 1);
				else
					falsePositive.put(pairs.getKey(), falsePositive.containsKey(pairs.getKey()) ? falsePositive.get(pairs.getKey()) + 1 : 1);
			} else
				falseNegative.put(pairs.getKey(), falseNegative.containsKey(pairs.getKey()) ? falseNegative.get(pairs.getKey()) + 1 : 1);
			saveCRFOutput.put(pairs.getKey(), pairs.getValue().getWordAsString());
		}

		if (testWithFailure)
			for (int i = 0; i < xmlAttUsed.length; i++)
				if (!xmlAttUsed[i])
					if (inHashMap(xmlAttName[i]) != null) {
						double fmeasure = 0;
						double recall = 0;
						double precision = 0;

						precisionPerLabel.put(xmlAttName[i], precisionPerLabel.containsKey(xmlAttName[i]) ? precisionPerLabel.get(xmlAttName[i]) + precision
								: precision);
						recallPerLabel.put(xmlAttName[i], recallPerLabel.containsKey(xmlAttName[i]) ? recallPerLabel.get(xmlAttName[i]) + recall : recall);
						fMeasurePerLabel.put(xmlAttName[i], fMeasurePerLabel.containsKey(xmlAttName[i]) ? fMeasurePerLabel.get(xmlAttName[i]) + fmeasure
								: fmeasure);
						CountPerLabel.put(xmlAttName[i], CountPerLabel.containsKey(xmlAttName[i]) ? CountPerLabel.get(xmlAttName[i]) + 1 : 1);

						falseNegative.put(xmlAttName[i], falseNegative.containsKey(xmlAttName[i]) ? falseNegative.get(xmlAttName[i]) + 1 : 1);
					}
		BufferedWriter incorrectWriter = new BufferedWriter(new FileWriter(new File(toSVN + toLogs + "/" + folderName + " pipeline " + fileName + ".txt")));
		writeOutputIntoFile(incorrectWriter, saveCRFOutput);
		incorrectWriter.close();

	}

	private static void writeOutputIntoFile(BufferedWriter incorrectWriter, HashMap<String, String> crfOutput) {
		try {
			incorrectWriter.append("XMLAttribute\tTessAtt\tprec\treca\tfmeas\txmlText\tTesseractText\n");
			DecimalFormat df = new DecimalFormat("#.##");
			for (int i = 0; i < xmlAttName.length; i++)
				incorrectWriter.append(xmlStuff[i] + (xmlStuff[i].length() > 8 ? "\t" : "\t\t") + xmlAttName[i] + "\t" + df.format(prec[i]) + "\t"
						+ df.format(reca[i]) + "\t" + df.format(prec[i]) + "\t" + xmlAtts.get(xmlStuff[i]) + "\t" + crfOutput.get(xmlAttName[i]) + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String inHashMap(String label) {
		if (label.equals("FN")) {
			xmlAttUsed[0] = true;
			return xmlAtts.get("First Name");
		} else if (label.equals("LN")) {
			xmlAttUsed[1] = true;
			return xmlAtts.get("Last Name");
		} else if (label.equals("ST")) {
			xmlAttUsed[2] = true;
			return xmlAtts.get("Street Address");
		} else if (label.equals("PLZ")) {
			xmlAttUsed[3] = true;
			return xmlAtts.get("Postal Code");
		} else if (label.equals("ORT")) {
			xmlAttUsed[4] = true;
			return xmlAtts.get("City");
		} else if (label.equals("I-TN")) {
			xmlAttUsed[5] = true;
			return xmlAtts.get("Phone");
		} else if (label.equals("I-FN")) {
			xmlAttUsed[6] = true;
			return xmlAtts.get("Fax");
		} else if (label.equals("I-MN")) {
			xmlAttUsed[7] = true;
			return xmlAtts.get("Phone.Mobile");
		} else if (label.equals("EMA")) {
			xmlAttUsed[8] = true;
			return xmlAtts.get("E-mail");
		}
		/*
		 * else if (label.equals("WEB")) return xmlAtts.get("");
		 */
		else if (label.equals("ORG")) {
			xmlAttUsed[9] = true;
			return xmlAtts.get("Company");
		} else if (label.equals("TIT")) {
			xmlAttUsed[10] = true;
			return xmlAtts.get("Title");
		}
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
