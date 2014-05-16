package ch.fhnw.imvs8.businesscardreader.ner;

import java.io.BufferedReader;
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
public class getLogs {
	private int cardCount; // Count of business cards which were tested
	private int cardCorrectCount; // Count of cards which are correct

	private double[] labelPercentage; // percentage per label
	private int[] countPerLabel; // count per label
	private HashMap<String, Integer> labelPosition = new HashMap<>(); // position of the labels in
													// the array

	private boolean[] cardCorrect; // count of cards which are correct
	private ArrayList<String> cardCorrectLabel = new ArrayList<>(); // String of labels

	/**
	 * create an object of the logs with all the labels as a String
	 * 
	 * @param labels
	 *            name of all the labels
	 * @throws IOException
	 */
	public getLogs(String[] labels) throws IOException {
		
		for (int labelPos = 0; labelPos < labels.length; labelPos++) {
			labelPosition.put(labels[labelPos], labelPos);
		}
		countPerLabel = new int[labels.length];
		labelPercentage = new double[labels.length];

		// read the labels which define if a card is totally correct
		String currentLine;
		BufferedReader reader = new BufferedReader(new FileReader("cardCorrectLabels.txt"));

		while ((currentLine = reader.readLine()) != null) {
			cardCorrectLabel.add(currentLine);
		}

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
	public void addToLogs(String label, double percentage) {
		int pos = labelPosition.get(label);
		labelPercentage[pos] += percentage;
		countPerLabel[pos]++;

		for (int i = 0; i < cardCorrectLabel.size(); i++) {
			if (cardCorrectLabel.get(i).equals(label)) {
				cardCorrect[i] = true;
			}
		}
	}

	/**
	 * if a new businesscard starts this method needs to be called otherwise
	 * logs will not correctly give you the count of allcorrect
	 */
	public void addCard() {
		boolean hasAllCorrect = true;
//		for (int i = 0; i < cardCorrect.length && hasAllCorrect; i++) {
//			if (!cardCorrect[i]) {
//				hasAllCorrect = false;
//			}
//		}

		if (hasAllCorrect) {
			cardCorrectCount++;
		}

		cardCount++;
		cardCorrect = new boolean[cardCorrectLabel.size()];
	}

	/**
	 * returns the percentage of how confident the value is. This percentage
	 * says nothing about how correct a label is.
	 * 
	 * @return a list of all the double values
	 */
	public double[] getPercentagePerLabel() {
		double[] result = new double[labelPercentage.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = labelPercentage[i] / countPerLabel[i];
		}
		return result;
	}

	/**
	 * how many cards had all labels correct which were specified
	 * 
	 * @return percentage of cards who are all correct
	 */
	public double getPercentageCardsCorrect() {
		return cardCount / (double) cardCorrectCount;
	}

}
