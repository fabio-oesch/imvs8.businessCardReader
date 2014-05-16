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
import ch.fhnw.imvs8.businesscardreader.ner.NamedEntity;

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

	public static void addFeatures(HashMap<String, NamedEntity> tokens) throws MalformedURLException {
		Iterator<NamedEntity> it = tokens.values().iterator();

		NameType name = new NameType();
		AddressFeature address = new AddressType();
		address.setCharset("UTF-8");

		while (it.hasNext()) {
			NamedEntity entity = it.next();
			
			switch (entity.getLabel()) {
			case "FN":
				name.setGivenName(entity.getEntity());
				break;
			case "LN":
				name.setFamilyName(entity.getEntity());
				break;
			case "TIT":
				name.addHonorificPrefix(entity.getEntity());
				break;
			case "ST":
				address.setStreetAddress(entity.getEntity());
				break;
			case "ORT":
				address.setRegion(entity.getEntity());
				break;
			case "PLZ":
				address.setPostalCode(entity.getEntity());
				break;
			case "EMA":
				EmailFeature email = new EmailType();
				email.setEmail(entity.getEntity());
				vcard.addEmail(email);
				break;
			case "WEB":
				vcard.addURL(new URLType(new URL(entity.getEntity())));
				break;
			case "ORG":
				OrganizationFeature organizations = new OrganizationType();
				organizations.addOrganization(entity.getEntity());
				vcard.setOrganizations(organizations);
				break;
			case "I-TW":
				TelephoneFeature telephoneWork = new TelephoneType();
				telephoneWork.setCharset("UTF-8");
				telephoneWork.setTelephone(entity.getEntity());
				telephoneWork.addTelephoneParameterType(TelephoneParameterType.WORK);
				vcard.addTelephoneNumber(telephoneWork);
				break;
			case "I-TF":
				TelephoneFeature telephoneFax = new TelephoneType();
				telephoneFax.setCharset("UTF-8");
				telephoneFax.setTelephone(entity.getEntity());
				telephoneFax.addTelephoneParameterType(TelephoneParameterType.FAX);
				vcard.addTelephoneNumber(telephoneFax);
				break;
			case "I-TM":
				TelephoneFeature telephoneMobile = new TelephoneType();
				telephoneMobile.setCharset("UTF-8");
				telephoneMobile.setTelephone(entity.getEntity());
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
