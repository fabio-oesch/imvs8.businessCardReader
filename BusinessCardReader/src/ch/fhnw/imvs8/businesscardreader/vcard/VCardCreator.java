package ch.fhnw.imvs8.businesscardreader.vcard;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.cardme.db.MarkType;
import net.sourceforge.cardme.io.VCardWriter;
import net.sourceforge.cardme.vcard.EncodingType;
import net.sourceforge.cardme.vcard.LanguageType;
import net.sourceforge.cardme.vcard.VCard;
import net.sourceforge.cardme.vcard.VCardImpl;
import net.sourceforge.cardme.vcard.features.AddressFeature;
import net.sourceforge.cardme.vcard.features.EmailFeature;
import net.sourceforge.cardme.vcard.features.FormattedNameFeature;
import net.sourceforge.cardme.vcard.features.OrganizationFeature;
import net.sourceforge.cardme.vcard.features.TelephoneFeature;
import net.sourceforge.cardme.vcard.types.AddressType;
import net.sourceforge.cardme.vcard.types.EmailType;
import net.sourceforge.cardme.vcard.types.FormattedNameType;
import net.sourceforge.cardme.vcard.types.NameType;
import net.sourceforge.cardme.vcard.types.OrganizationType;
import net.sourceforge.cardme.vcard.types.TelephoneType;
import net.sourceforge.cardme.vcard.types.URLType;
import net.sourceforge.cardme.vcard.types.parameters.ExtendedParameterType;
import net.sourceforge.cardme.vcard.types.parameters.ParameterTypeStyle;
import net.sourceforge.cardme.vcard.types.parameters.TelephoneParameterType;
import ch.fhnw.imvs8.businesscardreader.BusinessCard;
import ch.fhnw.imvs8.businesscardreader.BusinessCardField;
import ch.fhnw.imvs8.businesscardreader.ner.NEREngine;
import ch.fhnw.imvs8.businesscardreader.ner.LabeledWord;

/**
 * creates a vcard
 * 
 * @author olry
 * 
 */
public class VCardCreator {

	/**
	 * Greates a vCard String out of the provided words
	 * 
	 * if the web adress cannot be converted to a valid url, it gets ignored.
	 * @param words
	 * @return
	 */
	public static String getVCardString(Map<String, String> words) {
		 VCard vcard = new VCardImpl();
		 addFeatures(vcard,words);
		 VCardWriter writer = new VCardWriter();
		 writer.setVCard(vcard);
		 return writer.buildVCardString();
	}
	
	/**
	 * Converts the Business Card in a VCard
	 * 
	 * this method is equivalent to getVCardString(card.getWordsAsMap())
	 * 
	 * @param card Business Card to convert.
	 * @return vCard String
	 */
	public static String getVCardString(BusinessCard card) {
		return VCardCreator.getVCardString(card.getFieldsAsStringMap());
	}
	
	private static void addFeatures(VCard vcard, Map<String, String> words) {
		Iterator<String> it = words.keySet().iterator();

		NameType name = new NameType();
		AddressFeature address = new AddressType();
		address.setCharset("UTF-8");

		while (it.hasNext()) {
			String label = it.next();
			String word = words.get(label);
			
			switch (label) {
			case "FN":
				name.setGivenName(word);
				break;
			case "LN":
				name.setFamilyName(word);
				break;
			case "TIT":
				name.addHonorificPrefix(word);
				break;
			case "ST":
				address.setStreetAddress(word);
				break;
			case "ORT":
				address.setRegion(word);
				break;
			case "PLZ":
				address.setPostalCode(word);
				break;
			case "EMA":
				EmailFeature email = new EmailType();
				email.setEmail(word);
				vcard.addEmail(email);
				break;
			case "WEB":
				try {
					URLType url = new URLType(new URL(word));
					vcard.addURL(url);
				} catch(Exception e) {}
				break;
			case "ORG":
				OrganizationFeature organizations = new OrganizationType();
				organizations.addOrganization(word);
				vcard.setOrganizations(organizations);
				break;
			case "I-TW":
				TelephoneFeature telephoneWork = new TelephoneType();
				telephoneWork.setCharset("UTF-8");
				telephoneWork.setTelephone(word);
				telephoneWork.addTelephoneParameterType(TelephoneParameterType.WORK);
				vcard.addTelephoneNumber(telephoneWork);
				break;
			case "I-TF":
				TelephoneFeature telephoneFax = new TelephoneType();
				telephoneFax.setCharset("UTF-8");
				telephoneFax.setTelephone(word);
				telephoneFax.addTelephoneParameterType(TelephoneParameterType.FAX);
				vcard.addTelephoneNumber(telephoneFax);
				break;
			case "I-TM":
				TelephoneFeature telephoneMobile = new TelephoneType();
				telephoneMobile.setCharset("UTF-8");
				telephoneMobile.setTelephone(word);
				telephoneMobile.addTelephoneParameterType(TelephoneParameterType.CELL);
				vcard.addTelephoneNumber(telephoneMobile);
				break;
			default:
				break;
			}
		}
		vcard.setName(name);
		String first = words.get("FN");
		String last = words.get("LN");
		vcard.setFormattedName(new FormattedNameType(first + " "+last));
		vcard.addAddress(address);
	}

}
