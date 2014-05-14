package ch.imvs8.businesscardreader;

import static org.junit.Assert.*;

import org.junit.Test;

import ch.fhnw.imvs8.businesscardreader.BusinessCardReader;

public class BusinessCardReaderIntegrationTest {

	@Test
	public void EmptyFolderTest() {
		try{
			BusinessCardReader reader = new BusinessCardReader("test_data/EmptyFolderTest");
		} catch(Exception e) {
			Exception cause = (Exception)e.getCause();
			
			assertTrue("Not expected Error", cause.getMessage().startsWith("Invalid or missing files in folder:"));
		}
	}
	
	@Test
	public void MissingModelTest() {
		try{
			BusinessCardReader reader = new BusinessCardReader("test_data/MissingCRFModelTest");
		} catch(Exception e) {
			Exception cause = (Exception)e.getCause();
			
			assertTrue("Not expected Error", cause.getMessage().startsWith("model file not found:"));
		}
	}
	
	public void ValidRuntTest() {
		
		try{ 
			BusinessCardReader reader = new BusinessCardReader("test_data/ValidTest");
			reader.readImage("test_data/");
		}
		catch(Exception e) {
			fail("Exception was thrown. Exception: "+e.getMessage());
		}
	}

}
