package ch.fhnw.imvs8.businesscardreader.vcard;

import net.sourceforge.cardme.io.VCardWriter;
import net.sourceforge.cardme.vcard.VCard;
import net.sourceforge.cardme.vcard.VCardImpl;
import net.sourceforge.cardme.vcard.VCardVersion;
import net.sourceforge.cardme.vcard.features.AddressFeature;
import net.sourceforge.cardme.vcard.types.AddressType;
import net.sourceforge.cardme.vcard.types.FormattedNameType;
import net.sourceforge.cardme.vcard.types.NameType;
import net.sourceforge.cardme.vcard.types.VersionType;
import net.sourceforge.cardme.vcard.types.parameters.AddressParameterType;
import net.sourceforge.cardme.vcard.types.parameters.XAddressParameterType;

/**
 * 
 * @author O Lry
 * 
 *         het alles mann
 *         http://sourceforge.net/apps/mediawiki/cardme/index.php?
 *         title=Main_Page
 */
public class Example {
	static VCard vcard = new VCardImpl();

	public static void main(String[] args) {
		vcard.setVersion(new VersionType(VCardVersion.V3_0));

		NameType name = new NameType();
		name.setFamilyName("Doe");
		name.setGivenName("John");
		name.addHonorificPrefix("Mr.");

		vcard.setName(name);
		vcard.setFormattedName(new FormattedNameType("John \"Johny\" Doe"));

		setAdresse();

		// Assume variable vcard has already been created.

		VCardWriter writer = new VCardWriter();
		writer.setVCard(vcard);
		String vString = writer.buildVCardString();
		System.out.println(vString);
	}

	public static void setAdresse() {
		AddressFeature address1 = new AddressType();
		address1.setCharset("UTF-8");
		address1.setExtendedAddress("");
		address1.setCountryName("U.S.A.");
		address1.setLocality("New York");
		address1.setRegion("New York");
		address1.setPostalCode("NYC887");
		address1.setPostOfficeBox("25334");
		address1.setStreetAddress("South cresent drive, Building 5, 3rd floor");
		address1.addAddressParameterType(AddressParameterType.HOME);
		address1.addAddressParameterType(AddressParameterType.PARCEL);
		address1.addAddressParameterType(AddressParameterType.PREF);
		address1.addExtendedAddressParameterType(new XAddressParameterType("CUSTOM-PARAM-TYPE"));
		address1.addExtendedAddressParameterType(new XAddressParameterType("CUSTOM-PARAM-TYPE", "WITH-CUSTOM-VALUE"));

		vcard.addAddress(address1);
	}

}
