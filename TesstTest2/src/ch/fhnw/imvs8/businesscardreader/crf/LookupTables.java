package ch.fhnw.imvs8.businesscardreader.crf;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class LookupTables {
	public final static String PRENAME_FILE = "";
	public final static String LASTNAME_FILE = "";
	public final static String ZIP_FILE = "";
	public final static String ROADNAME_FILE = "";
	
	private final String folder;
	private final Set<String> preNames;
	private final Set<String> lastNames;
	private final Set<String> zips;
	private final Set<String> roadNames;
	
	public LookupTables(String folder) throws Exception {
		this.folder = folder;
		
		try {
			this.preNames = createSet(this.folder + "/"+ PRENAME_FILE);
			this.lastNames = createSet(this.folder + "/"+ LASTNAME_FILE);
			this.zips = createSet(this.folder + "/"+ PRENAME_FILE);
			this.roadNames = createSet(this.folder + "/"+ PRENAME_FILE);
		} 
		catch(Exception e) {
			StringBuilder b = new StringBuilder("Invalid files in folder: ");
			b.append(folder);
			
			throw new Exception(b.toString());
		}
	}
	
	public static Set<String> createSet(String file) throws IOException {
		HashSet<String> answer = new HashSet<>(1000);	//heuristic, generally large files, so could be more!
		BufferedReader r = new BufferedReader(new FileReader(file));
		String line = null;
		
		while((line = r.readLine()) != null)
			answer.add(line);
		
		return answer;
	}
}
