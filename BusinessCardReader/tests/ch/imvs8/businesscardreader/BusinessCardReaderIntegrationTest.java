package ch.imvs8.businesscardreader;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.Map;

import org.junit.Test;

import ch.fhnw.imvs8.businesscardreader.BusinessCardReader;
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
			System.out.println(e.getMessage());
			assertTrue("Not expected Error", e.getMessage().startsWith("model file not found:"));
		}
	}
	
	@Test
	public void ValidRunTest() {
		
		try{ 
			BusinessCardReader reader = new BusinessCardReader("unittest_data/ValidTest");
			Map<String,LabeledWord> entities = reader.readImage("unittest_data/validimage.jpg");
			assertSame("didn't find enough or too much", 4, entities.size());
			Iterator<LabeledWord> it = entities.values().iterator();
			while(it.hasNext()) {
				LabeledWord e = it.next();
				System.out.println(e.getLabel() + " " + e.getWordAsString() + " "+ e.getConfidence());
			}
		}
		catch(Exception e) {
			fail("Exception was thrown. Exception: "+e.getMessage());
		}
	}

}
