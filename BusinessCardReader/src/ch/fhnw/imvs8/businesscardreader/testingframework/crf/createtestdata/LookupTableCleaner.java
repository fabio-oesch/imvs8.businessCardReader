package ch.fhnw.imvs8.businesscardreader.testingframework.crf.createtestdata;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import ch.fhnw.imvs8.businesscardreader.ner.FeatureCreator;
import ch.fhnw.imvs8.businesscardreader.ner.LookupTables;
import ch.fhnw.imvs8.businesscardreader.ner.stemming.SimpleGermanStemming;
import ch.fhnw.imvs8.businesscardreader.ner.stemming.StemmingStrategy;

public class LookupTableCleaner {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		/*StemmingStrategy strat = new GermanStemming();
		String path = "lookup_tables/";
		String[] files = {"domains.txt","faxwords.txt","fixnetprefix.txt","lastnames.txt","mobileprefix.txt","mobilewords.txt","places.txt","prenames.txt","streetnames.txt","telwords.txt","ziplist.txt",};
		
		for(String f :files) {
			bla(path+f,strat);
		}*/
		//addCH();
		LookupTables table = new LookupTables("lookup_tables");
		FeatureCreator creator = new FeatureCreator(table, new SimpleGermanStemming());
		//System.out.println(creator.createLine("Robert"));
	}
	
	private static void addCH() throws Exception{
		BufferedReader r = new BufferedReader(new FileReader("lookup_tables/ziplist.txt"));
		
		LinkedList<String> list = new LinkedList<>();
		String line = null;
		while ((line = r.readLine()) != null)
			list.add(line);
		
		BufferedWriter out = new BufferedWriter(new FileWriter("lookup_tables/ziplist.txt"));
		Iterator<String> it = list.iterator();
		while(it.hasNext()) 
			out.write(it.next()+"\n");
		
		it = list.iterator();
		while(it.hasNext())
		{
			out.write("ch-"+it.next());
			if(it.hasNext())
				out.write("\n");
		}
		out.close();
	}
	
	private static void bla(String file, StemmingStrategy strat) throws IOException {
		
		TreeSet<String> set= (TreeSet<String>) createSet(file);
		Iterator<String> it = set.iterator();
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		
		while(it.hasNext()) {
			String word = strat.stemWord(it.next());
			out.write(word);
			
			if(it.hasNext())
				out.write("\n");
		}
		
		out.close();
	}
	
	private static Set<String> createSet(String file) throws IOException {
		TreeSet<String> answer = new TreeSet<>();
		File f = new File(file);
		System.out.println(f.exists());
		BufferedReader r = new BufferedReader(new FileReader(file));
		String line = null;

		while ((line = r.readLine()) != null)
			answer.add(line);

		r.close();

		return answer;
	}

}
