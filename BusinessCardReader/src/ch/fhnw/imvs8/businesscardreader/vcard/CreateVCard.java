package ch.fhnw.imvs8.businesscardreader.vcard;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import net.sourceforge.cardme.io.VCardWriter;
import net.sourceforge.cardme.vcard.VCard;
import net.sourceforge.cardme.vcard.VCardImpl;
import net.sourceforge.cardme.vcard.features.AddressFeature;
import net.sourceforge.cardme.vcard.features.EmailFeature;
import net.sourceforge.cardme.vcard.features.OrganizationFeature;
import net.sourceforge.cardme.vcard.features.TelephoneFeature;
import net.sourceforge.cardme.vcard.types.AddressType;
import net.sourceforge.cardme.vcard.types.EmailType;
import net.sourceforge.cardme.vcard.types.NameType;
import net.sourceforge.cardme.vcard.types.OrganizationType;
import net.sourceforge.cardme.vcard.types.TelephoneType;
import net.sourceforge.cardme.vcard.types.URLType;
import net.sourceforge.cardme.vcard.types.parameters.TelephoneParameterType;
import ch.fhnw.imvs8.businesscardreader.ner.NEREngine;
import ch.fhnw.imvs8.businesscardreader.ner.LabeledWord;

/**
 * creates a vcard
 * 
 * @author olry
 * 
 */
public class CreateVCard {
	private static VCard vcard = new VCardImpl();

	public VCard createVCard() throws IOException {
		HashMap<String, String> br = null;
		VCardWriter writer = new VCardWriter();
		writer.setVCard(vcard);
		String vString = writer.buildVCardString();
		System.out.println(vString);
		return vcard;
	}

	public static void addFeatures(HashMap<String, LabeledWord> tokens) throws MalformedURLException {
		Iterator<LabeledWord> it = tokens.values().iterator();

		NameType name = new NameType();
		AddressFeature address = new AddressType();
		address.setCharset("UTF-8");

		while (it.hasNext()) {
			LabeledWord entity = it.next();
			
			switch (entity.getLabel()) {
			case "FN":
				name.setGivenName(entity.getWordAsString());
				break;
			case "LN":
				name.setFamilyName(entity.getWordAsString());
				break;
			case "TIT":
				name.addHonorificPrefix(entity.getWordAsString());
				break;
			case "ST":
				address.setStreetAddress(entity.getWordAsString());
				break;
			case "ORT":
				address.setRegion(entity.getWordAsString());
				break;
			case "PLZ":
				address.setPostalCode(entity.getWordAsString());
				break;
			case "EMA":
				EmailFeature email = new EmailType();
				email.setEmail(entity.getWordAsString());
				vcard.addEmail(email);
				break;
			case "WEB":
				vcard.addURL(new URLType(new URL(entity.getWordAsString())));
				break;
			case "ORG":
				OrganizationFeature organizations = new OrganizationType();
				organizations.addOrganization(entity.getWordAsString());
				vcard.setOrganizations(organizations);
				break;
			case "I-TW":
				TelephoneFeature telephoneWork = new TelephoneType();
				telephoneWork.setCharset("UTF-8");
				telephoneWork.setTelephone(entity.getWordAsString());
				telephoneWork.addTelephoneParameterType(TelephoneParameterType.WORK);
				vcard.addTelephoneNumber(telephoneWork);
				break;
			case "I-TF":
				TelephoneFeature telephoneFax = new TelephoneType();
				telephoneFax.setCharset("UTF-8");
				telephoneFax.setTelephone(entity.getWordAsString());
				telephoneFax.addTelephoneParameterType(TelephoneParameterType.FAX);
				vcard.addTelephoneNumber(telephoneFax);
				break;
			case "I-TM":
				TelephoneFeature telephoneMobile = new TelephoneType();
				telephoneMobile.setCharset("UTF-8");
				telephoneMobile.setTelephone(entity.getWordAsString());
				telephoneMobile.addTelephoneParameterType(TelephoneParameterType.CELL);
				vcard.addTelephoneNumber(telephoneMobile);
				break;
			default:
				break;
			}
		}
		vcard.setName(name);
		vcard.addAddress(address);
	}

}
