package ch.fhnw.imvs8.businesscardreader.ner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Immutable object holding all lookup tables used for the feature vector.
 * Used by Object: FeatureCreator
 * 
 * @author jon
 * 
 */
public class LookupTables {
	public final static String FIRSTNAME_FILE = "prenames.txt";
	public final static String LASTNAME_FILE = "lastnames.txt";
	public final static String ZIP_FILE = "ziplist.txt";
	public final static String PLACE_FILE = "places.txt";
	public final static String ROADNAME_FILE = "streetnames.txt";
	public final static String DOMAIN_FILE ="domains.txt";
	public final static String MOBILEWORDS_FILE ="mobilewords.txt";
	public final static String TELWORDS_FILE ="telwords.txt";
	public final static String FAXWORDS_FILE="faxwords.txt";
	public final static String MOBILEPREFIX_FILE ="mobileprefix.txt";
	public final static String FIXNETPREFIX_FILE="fixnetprefix.txt";
	
	private final String folder;
	private final Set<String> firstNames;
	private final Set<String> lastNames;
	private final Set<String> zips;
	private final Set<String> places;
	private final Set<String> roadNames;
	private final Set<String> mobileWords;
	private final Set<String> telWords;
	private final Set<String> faxWords;
	private final Set<String> mobilePrefix;
	private final Set<String> fixnetPrefix;
	
	private final List<String> domains;

	public LookupTables(String folder) throws Exception {
		this.folder = folder;

		try {
			this.firstNames = createSet(this.folder + File.separator + FIRSTNAME_FILE);
			this.lastNames = createSet(this.folder + File.separator + LASTNAME_FILE);
			this.places = createSet(this.folder + File.separator + PLACE_FILE);
			this.zips = createSet(this.folder + File.separator + ZIP_FILE);
			this.roadNames = createSet(this.folder + File.separator + ROADNAME_FILE);
			mobileWords = createSet(this.folder + File.separator + MOBILEWORDS_FILE);
			telWords = createSet(this.folder + File.separator + TELWORDS_FILE);
			faxWords = createSet(this.folder + File.separator + FAXWORDS_FILE);
			mobilePrefix = createSet(this.folder + File.separator + MOBILEPREFIX_FILE);
			fixnetPrefix = createSet(this.folder + File.separator + FIXNETPREFIX_FILE);
			
			this.domains = createList(this.folder + File.separator + DOMAIN_FILE);
			
		} catch (Exception e) {
			StringBuilder b = new StringBuilder("Invalid or missing files in folder: ");
			b.append(folder);
			b.append("\nProblem:");
			b.append(e.getMessage());
			b.append("\n");
			b.append("\nExpected files in folder:\n");
			b.append(FIRSTNAME_FILE);
			b.append("\n");
			b.append(LASTNAME_FILE);
			b.append("\n");
			b.append(PLACE_FILE);
			b.append("\n");
			b.append(ZIP_FILE);
			b.append("\n");
			b.append(ROADNAME_FILE);
			b.append("\n");
			b.append(DOMAIN_FILE);
			b.append("\n");
			b.append(MOBILEWORDS_FILE);
			b.append("\n");
			b.append(TELWORDS_FILE);
			b.append("\n");
			b.append(FAXWORDS_FILE);
			b.append("\n");
			b.append(MOBILEPREFIX_FILE);
			b.append("\n");
			b.append(FIXNETPREFIX_FILE);

			throw new Exception(b.toString());
		}
	}

	public Set<String> getPrenameSet() {
		return this.firstNames;
	}

	public Set<String> getLastnameSet() {
		return this.lastNames;
	}

	public Set<String> getZipSet() {
		return this.zips;
	}

	public Set<String> getRoadnameSet() {
		return this.roadNames;
	}

	public Set<String> getPlacesSet() {
		return this.places;
	}
	
	/**
	 * Set of words typically leading a mobile number
	 * @return
	 */
	public Set<String> getMobileWordSet() {
		return this.mobileWords;
	}
	
	/**
	 * Set of words typically leading a work/fixnet telephone number
	 * @return
	 */
	public Set<String> getTelWordSet() {
		return this.telWords;
	}
	
	/**
	 * Set of words typically leading a fax/fixnet telephone number
	 * @return
	 */
	public Set<String> getFaxWordSet() {
		return this.faxWords;
	}
	
	/**
	 * Set of telephone number prefixes (as string) found in mobile numbers
	 * @return
	 */
	public Set<String> getMobilePrefixSet() {
		return this.mobilePrefix;
	}
	
	/**
	 * Set of telephone number prefixes (as string) found in fixnet numbers
	 * @return
	 */
	public Set<String> getFixnetPrefixSet() {
		return this.fixnetPrefix;
	}
	
	/**
	 * Set of telephone number prefixes (as string) found in fixnet numbers
	 * @return
	 */
	public List<String> getDomainsList() {
		return this.domains;
	}

	private static List<String> createList(String file) throws IOException {
		LinkedList<String> list = new LinkedList<>();
		BufferedReader r = new BufferedReader(new FileReader(file));
		
		String line = null;
		while ((line = r.readLine()) != null)
			list.add(line);
		
		
		return new ArrayList<String>(list);
	}
	
	private static Set<String> createSet(String file) throws IOException {
		TreeSet<String> answer = new TreeSet<>();
		BufferedReader r = new BufferedReader(new FileReader(file));
		String line = null;

		while ((line = r.readLine()) != null)
			answer.add(line);

		r.close();

		return answer;
	}
}
