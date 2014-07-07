package ch.fhnw.imvs8.businesscardreader.testingframework.crf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * create logs for the crf output
 * 
 * @author O Lry
 * 
 */
public class CRFLogGenerator {
	private int cardCount; // Count of business cards which were tested
	private int cardCorrectCount; // Count of cards which are correct

	private int[] falsePositivesPerLabel;
	private int[] falseNegativesPerLabel;

	private int[] falsePositivesPerLabelPerCard;
	private int[] falseNegativesPerLabelPerCard;

	private int[] correctPerLabel; // percentage per label
	private int[] countPerLabel; // count per label
	private HashMap<String, Integer> labelPosition = new HashMap<>(); // position
																		// of
																		// the
																		// labels
																		// in
	// the array
	private ArrayList<String> cardCorrectLabel = new ArrayList<>(); // String of
																	// labels

	/**
	 * create an object of the logs with all the labels as a String
	 * 
	 * @param labels
	 *            name of all the labels
	 * @throws IOException
	 */
	public CRFLogGenerator(String[] labels) throws IOException {

		for (int labelPos = 0; labelPos < labels.length; labelPos++)
			labelPosition.put(labels[labelPos], labelPos);
		countPerLabel = new int[labels.length];
		correctPerLabel = new int[labels.length];

		falsePositivesPerLabel = new int[labels.length];
		falseNegativesPerLabel = new int[labels.length];
		falsePositivesPerLabelPerCard = new int[labels.length];
		falseNegativesPerLabelPerCard = new int[labels.length];

		// read the labels which define if a card is totally correct
		String currentLine;
		BufferedReader reader = new BufferedReader(new FileReader("cardCorrectLabels.txt"));

		while ((currentLine = reader.readLine()) != null)
			cardCorrectLabel.add(currentLine);

		reader.close();

		addCard();
	}

	/**
	 * add a label with percentage to the logs
	 * 
	 * @param label
	 *            name of label
	 * @param percentage
	 *            percentage of how correct the label is
	 */
	public synchronized boolean addToLogs(String shouldLabel, String actualLabel, double percentage) {
		int pos = labelPosition.get(shouldLabel);
		if (actualLabel.equals(shouldLabel)) {
			correctPerLabel[pos]++;
			countPerLabel[pos]++;

			return true;
		} else {
			falseNegativesPerLabel[pos]++;
			int actualPos = labelPosition.get(actualLabel);
			falsePositivesPerLabel[actualPos]++;
			countPerLabel[pos]++;

			// for card correct stuff
			falseNegativesPerLabelPerCard[pos]++;
			falsePositivesPerLabelPerCard[actualPos]++;
			return false;
		}

	}

	/**
	 * if a new businesscard starts this method needs to be called otherwise
	 * logs will not correctly give you the count of allcorrect
	 */
	public void addCard() {
		boolean hasAllCorrect = true;
		for (int i = 0; i < cardCorrectLabel.size() && hasAllCorrect; i++) {
			int pos = labelPosition.get(cardCorrectLabel.get(i));
			if (falsePositivesPerLabelPerCard[pos] != 0 || falseNegativesPerLabelPerCard[pos] != 0)
				hasAllCorrect = false;
		}

		if (hasAllCorrect)
			cardCorrectCount++;

		cardCount++;
		falseNegativesPerLabelPerCard = new int[falseNegativesPerLabelPerCard.length];
		falsePositivesPerLabelPerCard = new int[falseNegativesPerLabelPerCard.length];
	}

	/**
	 * returns the percentage of how confident the value is. This percentage
	 * says nothing about how correct a label is.
	 * 
	 * @return a list of all the double values
	 */
	public double[] getPercentagePerLabel() {
		double[] result = new double[correctPerLabel.length];
		for (int i = 0; i < result.length; i++) {
			System.out.println(correctPerLabel[i] + " " + countPerLabel[i]);
			result[i] = correctPerLabel[i] / countPerLabel[i];
		}
		return result;
	}

	/**
	 * returns the percentage of how confident the value is. This percentage
	 * says nothing about how correct a label is.
	 * 
	 * @param writer
	 *            the file where the logs should be added
	 * @return a list of all the double values
	 * @throws IOException
	 */
	public double[] getPercentagePerLabel(BufferedWriter writer, String[] labels) throws IOException {
		double[] result = new double[correctPerLabel.length];
		writer.append("\nCorrect Count Per Label \n");
		for (int i = 0; i < result.length; i++) {
			writer.append(labels[i] + " " + correctPerLabel[i] + " " + countPerLabel[i] + "\n");
			result[i] = correctPerLabel[i] / (double) countPerLabel[i];
		}
		return result;
	}

	/**
	 * how many cards had all labels correct which were specified
	 * 
	 * @return percentage of cards who are all correct
	 */
	public double getHadAllLabelsPerCardCorrect() {
		return cardCorrectCount / (double) cardCount;
	}

	public double[] getPrecisionPerLabel(BufferedWriter writer, String[] labels) throws IOException {
		double[] result = new double[correctPerLabel.length];

		for (int i = 0; i < result.length; i++) {
			result[i] = correctPerLabel[i] / (double) (correctPerLabel[i] + falsePositivesPerLabel[i]);
			writer.append(labels[i] + " " + result[i] + "\n");
		}

		return result;
	}

	public double[] getRecallPerLabel(BufferedWriter writer, String[] labels) throws IOException {
		double[] result = new double[correctPerLabel.length];

		for (int i = 0; i < result.length; i++) {
			result[i] = correctPerLabel[i] / (double) (correctPerLabel[i] + falseNegativesPerLabel[i]);
			writer.append(labels[i] + " " + result[i] + "\n");
		}

		return result;
	}

	public double[] getFMeasurePerLabel(BufferedWriter writer, String[] labels) throws IOException {
		double precision;
		double recall;
		double[] result = new double[correctPerLabel.length];

		for (int i = 0; i < result.length; i++) {
			System.out.println(countPerLabel[i] + " / " + correctPerLabel[i]);
			precision = correctPerLabel[i] / (double) (correctPerLabel[i] + falsePositivesPerLabel[i]);
			recall = correctPerLabel[i] / (double) (correctPerLabel[i] + falseNegativesPerLabel[i]);
			result[i] = 2 * (precision * recall) / (precision + recall);
			writer.append(labels[i] + " " + result[i] + "\n");
		}
		return result;
	}
}
