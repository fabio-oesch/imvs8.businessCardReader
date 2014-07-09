package ch.imvs8.businesscardreader;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import ch.fhnw.imvs8.businesscardreader.BusinessCard;
import ch.fhnw.imvs8.businesscardreader.BusinessCardReader;
import ch.fhnw.imvs8.businesscardreader.BusinessCardField;
import ch.fhnw.imvs8.businesscardreader.ner.LabeledWord;

public class BusinessCardReaderIntegrationTest {

	@Test
	public void EmptyFolderTest() {
		try{
			BusinessCardReader reader = new BusinessCardReader("unittest_data/EmptyFolderTest");
		} catch(Exception e) {
			
			assertTrue("Not expected Error", e.getMessage().startsWith("Invalid or missing files in folder:"));
		}
	}
	
	@Test
	public void MissingModelTest() {
		try{
			BusinessCardReader reader = new BusinessCardReader("unittest_data/MissingCRFModelTest");
		} catch(Exception e) {
			assertTrue("Not expected Error", e.getMessage().startsWith("model file not found:"));
		}
	}
	
	@Test
	public void ValidRunTest() {
		try { 
			BusinessCardReader reader = new BusinessCardReader("unittest_data/ValidTest");
			BusinessCard card = reader.readImage("unittest_data/validimage.jpg");
			//assertSame("didn't find enough or too much", 16, entities.size());
			
			assertEquals("Max", card.getField("First Name"));
			assertEquals("Seldenstrasse 1", card.getField("Street"));
			for(int i = 0; i < BusinessCard.FIELD_LABELS.length;i++) {
				BusinessCardField e = card.getField(BusinessCard.FIELD_LABELS[i]);
				System.out.println(e.getLabel() + ": " + e.getField() + " ");
			}

		}
		catch(Exception e) {
			e.printStackTrace();
			fail("Exception was thrown. Exception: "+e.getMessage());
			
		}
	
	}

}
