package ch.imvs8.businesscardreader.crf;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import org.junit.Test;

import ch.fhnw.imvs8.businesscardreader.crf.FeatureCreator;
import ch.fhnw.imvs8.businesscardreader.crf.LookupTables;
import ch.fhnw.imvs8.businesscardreader.ocr.AnalysisResult;

public class FeatureCreatorTest {

	private static AnalysisResult createValidExample() {
		ArrayList<String> words = new ArrayList<>(14);
		words.add("Max");
		words.add("Müller");
		words.add("Seidenstrasse");
		words.add("1");
		words.add("5200");
		words.add("Brugg");
		words.add("Mobile");
		words.add("+41");
		words.add("79");
		words.add("111");
		words.add("11");
		words.add("11");
		words.add("max.müller@aha.com");
		words.add("www.aha.com");
		
		return new AnalysisResult(null,words,null,null);
	}
	@Test
	public void completeTest() {
		final String tmpFile = "featureCreatorTest.txt";
		final String validationFile = "test_data/FeatureCreatorValidation.txt";
		
		BufferedReader expectedReader = null;
		BufferedReader actualReader =null;
		try {
			LookupTables table = new LookupTables("lookup_tables");
			FeatureCreator creator = new FeatureCreator(table);
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
		if(f.exists())
			f.delete();
	}

}
