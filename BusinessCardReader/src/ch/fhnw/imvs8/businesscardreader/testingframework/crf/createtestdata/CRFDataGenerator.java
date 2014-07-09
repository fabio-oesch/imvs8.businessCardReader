package ch.fhnw.imvs8.businesscardreader.testingframework.crf.createtestdata;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;

import ch.fhnw.imvs8.businesscardreader.BusinessCard;
import ch.fhnw.imvs8.businesscardreader.BusinessCardReader;

public class CRFDataGenerator {

	public static void main(String[] args) throws Exception {
		BusinessCardReader reader = new BusinessCardReader(".");
		FileWriter writer = new FileWriter("traindata.txt");
		File dir = new File("/home/jon/dev/fuckingsvn/svn/testdata/business-cards/");
		String solution = "/solution";
		File[] dirs = dir.listFiles();
		
		Arrays.sort(dirs);
		
		for(File f :dirs) {
			System.out.println(f.getAbsolutePath());
			File solFolder = new File(f.getAbsolutePath()+solution);
			File[] files = solFolder.listFiles(new FilenameFilter() {
	
				@Override
				public boolean accept(File arg0, String arg1) {
					arg1.endsWith("");
					return arg1.endsWith("-image-raw.png");
				}
			});
			
			BusinessCard card = reader.readImage(files[0].getAbsolutePath());
			StringBuilder b = new StringBuilder(card.writeDebugOutput());
			b.append(",,,,,\n");
			writer.append(b.toString());
			writer.flush();
		}
		writer.close();
	}
}
