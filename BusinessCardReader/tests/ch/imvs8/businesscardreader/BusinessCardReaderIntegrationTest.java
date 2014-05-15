package ch.imvs8.businesscardreader;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.Map;

import org.junit.Test;

import ch.fhnw.imvs8.businesscardreader.BusinessCardReader;
import ch.fhnw.imvs8.businesscardreader.crf.NamedEntity;

public class BusinessCardReaderIntegrationTest {

	@Test
	public void EmptyFolderTest() {
		try{
			BusinessCardReader reader = new BusinessCardReader("test_data/EmptyFolderTest");
		} catch(Exception e) {
			
			assertTrue("Not expected Error", e.getMessage().startsWith("Invalid or missing files in folder:"));
		}
	}
	
	@Test
	public void MissingModelTest() {
		try{
			BusinessCardReader reader = new BusinessCardReader("test_data/MissingCRFModelTest");
		} catch(Exception e) {
			System.out.println(e.getMessage());
			assertTrue("Not expected Error", e.getMessage().startsWith("model file not found:"));
		}
	}
	
	@Test
	public void ValidRunTest() {
		
		try{ 
			BusinessCardReader reader = new BusinessCardReader("test_data/ValidTest");
			Map<String,NamedEntity> entities = reader.readImage("test_data/validimage.jpg");
			assertSame("didn't find enough or too much", 2, entities.size());
			Iterator<NamedEntity> it = entities.values().iterator();
			while(it.hasNext()) {
				NamedEntity e = it.next();
				System.out.println(e.tag + " " + e.entity + " "+ e.confidence);
			}
		}
		catch(Exception e) {
			fail("Exception was thrown. Exception: "+e.getMessage());
		}
	}

}
