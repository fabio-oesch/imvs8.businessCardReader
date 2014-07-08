package ch.imvs8.businesscardreader.ner;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import org.junit.Test;

import ch.fhnw.imvs8.businesscardreader.ner.FeatureCreator;
import ch.fhnw.imvs8.businesscardreader.ner.LookupTables;
import ch.fhnw.imvs8.businesscardreader.ner.stemming.GermanStemming;
import ch.fhnw.imvs8.businesscardreader.ocr.AnalysisResult;
import ch.fhnw.imvs8.businesscardreader.ocr.TesseractLine;

public class FeatureCreatorTest {
	final String tmpFile = "featureCreatorTest.txt";
	
	private static AnalysisResult createValidExample() {
		ArrayList<String> words = new ArrayList<>(16);
		words.add("Meisterfirma");
		words.add("AG");
		words.add("Max");
		words.add("Müller");
		words.add("Seidenstrasse");
		words.add("1");
		words.add("CH-5200");
		words.add("Brugg");
		words.add("Mobile");
		words.add("+41");
		words.add("79");
		words.add("111");
		words.add("11");
		words.add("11");
		words.add("max.müller@aha.com");
		words.add("www.aha.com");
		ArrayList<Integer> lineIndex = new ArrayList<>(16);
		lineIndex.add(0);
		lineIndex.add(0);
		lineIndex.add(1);
		lineIndex.add(1);
		lineIndex.add(2);
		lineIndex.add(2);
		lineIndex.add(3);
		lineIndex.add(3);
		lineIndex.add(4);
		lineIndex.add(4);
		lineIndex.add(4);
		lineIndex.add(4);
		lineIndex.add(4);
		lineIndex.add(4);
		lineIndex.add(5);
		lineIndex.add(6);
		ArrayList<Integer> colIndex = new ArrayList<>(16);
		colIndex.add(0);
		colIndex.add(1);
		colIndex.add(0);
		colIndex.add(1);
		colIndex.add(0);
		colIndex.add(1);
		colIndex.add(0);
		colIndex.add(1);
		colIndex.add(0);
		colIndex.add(1);
		colIndex.add(2);
		colIndex.add(3);
		colIndex.add(4);
		colIndex.add(5);
		colIndex.add(0);
		colIndex.add(0);
		
		ArrayList<Integer> totalColumns = new ArrayList<>(16);
		totalColumns.add(2);
		totalColumns.add(2);
		totalColumns.add(2);
		totalColumns.add(2);
		totalColumns.add(2);
		totalColumns.add(2);
		totalColumns.add(2);
		totalColumns.add(2);
		totalColumns.add(6);
		totalColumns.add(6);
		totalColumns.add(6);
		totalColumns.add(6);
		totalColumns.add(6);
		totalColumns.add(6);
		totalColumns.add(1);
		totalColumns.add(1);
		
		ArrayList<Float> confidences = new ArrayList<>(16);
		confidences.add(20.0f);
		confidences.add(30.0f);
		confidences.add(40.0f);
		confidences.add(50.0f);
		confidences.add(60.0f);
		confidences.add(70.0f);
		confidences.add(80.0f);
		confidences.add(90.0f);
		confidences.add(80.0f);
		confidences.add(80.0f);
		confidences.add(80.0f);
		confidences.add(80.0f);
		confidences.add(80.0f);
		confidences.add(80.0f);
		confidences.add(80.0f);
		confidences.add(80.0f);
		
		return new AnalysisResult(null,words,null,confidences,7,lineIndex,colIndex,totalColumns);
	}
	@Test
	public void completeTest() {
		final String validationFile = "unittest_data/FeatureCreatorValidation.txt";
		
		BufferedReader expectedReader = null;
		BufferedReader actualReader =null;
		try {
			LookupTables table = new LookupTables("lookup_tables");
			FeatureCreator creator = new FeatureCreator(table, new GermanStemming());
			creator.createFeatures(createValidExample(), tmpFile);
			
			//check file
			expectedReader = new BufferedReader(new FileReader(validationFile));
			actualReader = new BufferedReader(new FileReader(tmpFile));
			
			String expectedLine = null;
			String actualLine = null;
			while((expectedLine = expectedReader.readLine())!= null &
					(actualLine = actualReader.readLine()) != null) {
				assertEquals("Actual output differs from expected output",expectedLine, actualLine);
			}
			
			assertEquals("Files do not have the same length",expectedLine, actualLine);
		} catch (Exception e) {
			fail("Exception thrown: "+e.getMessage());
		}
		finally {
			try{
				expectedReader.close();
				actualReader.close();
				} catch(Exception e) { }
		}
		
		//cleanup
		File f = new File(tmpFile);
		System.out.println(f.getAbsolutePath());
		if(f.exists())
			f.delete();
	}
	
	@Test
	public void TestSingleFeature() {
		try {
			LookupTables table = new LookupTables("lookup_tables");
			FeatureCreator creator = new FeatureCreator(table, new GermanStemming());
			System.out.println(creator.createLine("direkt",0,1,0,1,90.0));
			
		} catch (Exception e) {
			fail("Exception thrown: "+e.getMessage());
		}
	}

}
