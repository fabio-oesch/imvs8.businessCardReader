package ch.fhnw.imvs8.businesscardreader.crf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Immutable object holding all lookuptables used in the CRF analysis
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

	private final String folder;
	private final Set<String> firstNames;
	private final Set<String> lastNames;
	private final Set<String> zips;
	private final Set<String> places;
	private final Set<String> roadNames;

	public LookupTables(String folder) throws Exception {
		this.folder = folder;

		try {
			this.firstNames = createSet(this.folder + "/" + FIRSTNAME_FILE);
			this.lastNames = createSet(this.folder + "/" + LASTNAME_FILE);
			this.places = createSet(this.folder + "/" + PLACE_FILE);
			this.zips = createSet(this.folder + "/" + ZIP_FILE);
			this.roadNames = createSet(this.folder + "/" + ROADNAME_FILE);
		} catch (Exception e) {
			StringBuilder b = new StringBuilder("Invalid or missing files in folder: ");
			b.append(folder);
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

			throw new Exception(b.toString());
		}
	}

	public Set<String> getPrenameList() {
		return this.firstNames;
	}

	public Set<String> getLastnameList() {
		return this.lastNames;
	}

	public Set<String> getZipList() {
		return this.zips;
	}

	public Set<String> getRoadnameList() {
		return this.roadNames;
	}

	public Set<String> getPlacesList() {
		return this.places;
	}

	public static Set<String> createSet(String file) throws IOException {
		HashSet<String> answer = new HashSet<>(1000); //heuristic, generally large files, so could be more!
		BufferedReader r = new BufferedReader(new FileReader(file));
		String line = null;

		while ((line = r.readLine()) != null)
			answer.add(line);

		r.close();

		return answer;
	}
}
