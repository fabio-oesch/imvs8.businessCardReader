package ch.imvs8.businesscardreader.vcard;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

import ch.fhnw.imvs8.businesscardreader.vcard.VCardCreator;

public class VCardCreatorTest {

	@Test
	public void test() {
		HashMap<String,String> data = new HashMap<>();
		data.put("FN", "Max");
		data.put("LN", "Muster");
		
		try{
			String s = VCardCreator.getVCardString(data);
		} catch(Exception e)
		{
			fail("exception thrown. Message: "+e.getMessage());
		}
	}

}
