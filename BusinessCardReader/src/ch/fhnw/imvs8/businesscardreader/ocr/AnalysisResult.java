package ch.fhnw.imvs8.businesscardreader.ocr;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;

/**
 * Represents the result of the OCR analysis. It is responsible for metadata
 * stuff
 * 
 * @author Jon
 * 
 */
public class AnalysisResult {
	private final ArrayList<String> words;
	private final ArrayList<Rectangle> boundingBoxes;
	private final ArrayList<Float> confidences;
	private final ArrayList<Integer> lineIndices;
	private final ArrayList<Integer> columnIndices;
	private final ArrayList<Integer> totWordInLine;
	private final int totLines;
	
	public AnalysisResult(File image,ArrayList<String> words, ArrayList<Rectangle> bBoxes, ArrayList<Float> conf,int totLines, ArrayList<Integer> lineIndices,ArrayList<Integer> columnIndices,ArrayList<Integer> totWordInLine) {
		this.totLines = totLines;
		this.words = words;
		this.boundingBoxes = bBoxes;
		this.confidences = conf;
		this.lineIndices = lineIndices;
		this.columnIndices = columnIndices;
		this.totWordInLine = totWordInLine;
	}
	/**
	 * @param image
	 *          Image 
	 * @param words
	 * 			List words 
	 * @param bBoxes
	 * 			List of Bounding boxes per word
	 * @param conf
	 * 			List of confidence values per word
	 */
	public AnalysisResult(File image, ArrayList<String> words, ArrayList<Rectangle> bBoxes, ArrayList<Float> conf, TesseractLine lines) {
		this.words = new ArrayList<>(words);
		this.boundingBoxes = new ArrayList<>(bBoxes);
		this.confidences = new ArrayList<>(conf);
		lineIndices = new ArrayList<>(words.size());
		columnIndices = new ArrayList<>(words.size());
		totWordInLine = new ArrayList<>(words.size());
		totLines = lines.textLines.size();
		initLineNumbering(lines);
		//this.cleanResults();
	}
	
	private void initLineNumbering(TesseractLine lines) {
		int lineNumber = 0;
		int wordPos = 0;
		int wordIndexStart = 0;
		for(int i = 0; i < words.size();i++) {
			Rectangle lineBB = lines.boundingBoxes.get(lineNumber);
			Rectangle wordBB = boundingBoxes.get(i);
			if(lineBB.contains(wordBB.getLocation())) {
				lineIndices.add(lineNumber);
				columnIndices.add(wordPos);
				wordPos++;
			} else {
				while(!lineBB.contains(wordBB.getLocation())) 
					lineBB = lines.boundingBoxes.get(++lineNumber);
				
				int wordCount = wordPos;
				for(int j = wordIndexStart;j < i;j++) 
					totWordInLine.add(wordCount);
				wordPos = 0;
				wordIndexStart = i;
				
				lineIndices.add(lineNumber);
				columnIndices.add(wordPos);
				wordPos++;
			}		
		}
		
		int wordCount = wordPos;
		for(int j = wordIndexStart;j < words.size();j++) 
			totWordInLine.add(wordCount);
		
	}

	public int getResultSize() {
		return words.size();
	}

	public String getWord(int index) {
		return words.get(index);
	}

	public Rectangle getBoundingBox(int index) {
		return boundingBoxes.get(index);
	}

	public Float getConfidence(int index) {
		return confidences.get(index);
	}
	
	public Integer getLineIndex(int index) {
		return lineIndices.get(index);
	}
	
	public Integer getColumnIndex(int index) {
		return columnIndices.get(index);
	}
	
	public Integer getTotalNumberOfColumnsInLine(int index) {
		return totWordInLine.get(index);
	}
	
	public Integer getTotalNumberOfLines() {
		return this.totLines;
	}
}
