package test.createTestData;

import java.awt.image.LookupOp;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ch.fhnw.imvs8.businesscardreader.crf.FeatureCreator;
import ch.fhnw.imvs8.businesscardreader.crf.LookupTables;
import ch.fhnw.imvs8.businesscardreader.crf.NamedEntity;
import ch.fhnw.imvs8.businesscardreader.crf.getLogs;
import ch.fhnw.imvs8.businesscardreader.crf.stemming.GermanStemming;

public class ModelGenerator {
	private static getLogs logs;
	private static String toCRF = "/usr/local/bin";
	private static String toModel = "model";
	
	public static void main(String[] args) throws Exception {
		testModel("/home/jon/dev/fuckingsvn/svn/testdata/crf-testdata/testdata.c1.test.csv", toModel);
		//createModel("/home/jon/dev/fuckingsvn/svn/testdata/crf-testdata/testdata.crf.v4_c1.csv","training","/home/jon/dev/fuckingsvn/svn/testdata/crf-testdata/template");
	}
	
	public static void createModel(String inFile,String trainingFile, String templateFile) throws Exception  {
		createFile(inFile,trainingFile);
		//Create
		Process process = new ProcessBuilder(toCRF+"/crf_learn",templateFile,trainingFile,"model").start();
		System.out.println("Waiting for learing proccess");
		process.waitFor();
		System.out.println("done");
	}
	
	public static void countFuckingWords(int lineNumber, String line) {
		String[] lineArr = line.split(" ");
		int should = 19;
	
			lineArr = line.split(" ");
			if (should != lineArr.length) {
				System.out.println(lineNumber + " " + lineArr.length);
				System.out.println(line);
			}
		
	}
	
	public static void createFile(String inFile,String testFile) throws Exception {
		int lineNumber = 1;
		LookupTables table = new LookupTables("lookup_tables");
		FeatureCreator creator = new FeatureCreator(table, new GermanStemming());
		
		BufferedReader reader = new BufferedReader(new FileReader(inFile));
		BufferedWriter out = new BufferedWriter(new FileWriter(testFile));
		
		String line = null;
		while((line = reader.readLine()) != null) {
			if(line.equals(",")) {
				out.write("\n");
			}
			else {

				String[] content = line.split(",");
				/*if (content.length > 2)
					System.out.println("fuck, more than 2 commas in: "+line);*/
				
				String word = content[0];
				
				String features = creator.createLine(word);
				out.write(features.trim()+ " "+content[1].trim());
				countFuckingWords(lineNumber, features.trim()+ " "+content[1].trim());
				out.write("\n");
				lineNumber++;
			}
		}
		
		reader.close();
		out.close();
		
	}
	
	public static void testModel(String testFile,String toModel) throws Exception {
		createFile(testFile,"testdata");
		readOutput("testdata");
		
	}
	
	private static void readOutput(String toTestData) throws IOException {
		Process process = new ProcessBuilder(toCRF + "/crf_test", "-v1", "-m", toModel + "/model", toTestData).start();
		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		logs = new getLogs(NamedEntity.LABELS);
		
		String line;
		while ((line = br.readLine()) != null) {
			if(!line.startsWith("#")) {
				if(line.length() < 2) {
					logs.addCard();
				} else {
					String[] lineArray = line.split("\t");
					if (lineArray.length > 2) {
						// only works when -v1 and -v2 is not set
						String labelAndConfidence = lineArray[lineArray.length - 1];
						int dashIndex = labelAndConfidence.indexOf('/');
						
						String label = labelAndConfidence.substring(0, dashIndex);
						double conf = Double.parseDouble(labelAndConfidence.substring(dashIndex+1));
						NamedEntity res = new NamedEntity(label,lineArray[0],conf);
						
						logs.addToLogs(res.getLabel(), res.getConfidence());
					}
				}
			}
	
		}
		
		System.out.println(logs.getPercentageCardsCorrect());
		
		String[] labels = NamedEntity.LABELS;
		double[] stuff = logs.getPercentagePerLabel();
		for(int i = 0;i< labels.length;i++ ) {
			System.out.println(labels[i]+ " "+ stuff[i]);
		}


	}
}
