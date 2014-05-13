package test.createTestData;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Stuff {

	public static void main(String[] args) throws IOException {
		BufferedReader r = new BufferedReader(new FileReader("in.txt"));
		FileWriter out = new FileWriter("hello.txt");
		String line = null;
		while((line = r.readLine())!= null) {
			String bla = line.substring(55,80);
			bla = bla.trim();
			out.write(bla+"\n");
		}
		out.close();r.close();
	}
}
