package ch.fhnw.imvs8.businesscardreader.testingframework.crf.createtestdata;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class CRFDataCleaner {

	
	
	public static void main(String[] args) throws IOException {
		BufferedReader r = new BufferedReader(new FileReader("good.txt"));
		BufferedReader crap = new BufferedReader(new FileReader("traindata.txt"));
		BufferedWriter out = new BufferedWriter(new FileWriter("fine.txt"));
		
		HashMap<String,String> cleanedData = new HashMap<>();
		
		String line = null;
		while((line = r.readLine()) != null) {
			if(!line.contains(",,,,")) {
				int lastCol2 = line.lastIndexOf(',');
				String label = (line.substring(lastCol2+1)).trim();
				final int csvColumns = 6;
				for(int j = csvColumns-2;j > 0;j--) {
					int lastCol = line.lastIndexOf(',');
					line = line.substring(0, lastCol).trim();
				}
				
				if(cleanedData.containsKey(line))
					System.out.println("fuck"+" "+line);
				else 
					cleanedData.put(line, label);
			}
		}
		int count = 0;
		line = null;
		while((line = crap.readLine()) != null) {
			if(!line.contains(",,,,")) {
				int lastCol = line.lastIndexOf(',');
				String label = (line.substring(lastCol+1)).trim();
				line = line.substring(0, lastCol);
				
				if(cleanedData.containsKey(line)) {
					//out.append(line+","+cleanedData.get(line)+"\n");
				} else {
					count++;
					//ok,card does not exist, kick it
					//while(",,,,".contains((line = crap.readLine())));
				}
			} else {
				//out.append(",,,,\n");
			}
		}
		
		System.out.println(count);
		out.close();
		
		
	}
}
