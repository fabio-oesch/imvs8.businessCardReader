package ch.fhnw.imvs8.businesscardreader.ocr;

import java.awt.Rectangle;
import java.util.ArrayList;

/**
 * Represents a line read by tesseract
 * 
 * It is used to find the line and the word position in a line
 * @author jon
 *
 */
public class TesseractLine {
	public ArrayList<String> textLines;
	public ArrayList<Rectangle> boundingBoxes;
}
