package ch.fhnw.imvs8.businesscardreader.testingframework.crf;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;

import ch.fhnw.imvs8.businesscardreader.BusinessCardReader;

public class CRFDataGenerator {

	public static void main(String[] args) throws Exception {
		BusinessCardReader reader = new BusinessCardReader(".");
		FileWriter writer = new FileWriter("traindata.txt");
		File dir = new File("/home/jon/dev/fuckingsvn/svn/testdata/business-cards/");
		String solution = "/solution";
		
		for(File f : dir.listFiles()) {
			File solFolder = new File(f.getAbsolutePath()+solution);
			File[] files = solFolder.listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File arg0, String arg1) {
					arg1.endsWith("");
					return arg1.endsWith("-image-raw.png");
				}
			});
			//StringBuilder b = reader.getCardWithTessdata(files[0].getAbsolutePath());
			StringBuilder b = null;
			b.append(",,,,,\n");
			writer.append(b.toString());
			writer.flush();
		}
		writer.close();
	}
}
