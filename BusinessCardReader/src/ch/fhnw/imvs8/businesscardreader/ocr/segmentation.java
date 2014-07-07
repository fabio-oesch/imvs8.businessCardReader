package ch.fhnw.imvs8.businesscardreader.ocr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import com.sun.xml.internal.ws.api.pipe.Engine;

import ch.fhnw.imvs8.businesscardreader.imagefilters.GenericFilterProcessor;
import ch.fhnw.imvs8.businesscardreader.imagefilters.GrayScaleFilter;
import ch.fhnw.imvs8.businesscardreader.imagefilters.LaplaceSharpenFilter;
import ch.fhnw.imvs8.businesscardreader.imagefilters.Phansalkar;

public class segmentation {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		final String cardsPrefix = "/home/jon/dev/fuckingsvn/svn/testdata/business-cards/";
		final String[] cards = {"christophe.meili@jaree.com/testimages/20131021_114720.jpg","c.stamm@fhbb.ch/testimages/IMG_20131114_154347.jpg","paul.beck@gmaare.migros.ch/testimages/IMG_20131129_113313.jpg","reto.kuenzler@swisstopo.ch/testimages/IMG_20131115_131731.jpg"};
		GenericFilterProcessor filters = new GenericFilterProcessor();
		filters.appendFilter(new GrayScaleFilter());
		filters.appendFilter(new Phansalkar());
		FileWriter writer = new FileWriter("segmentation.txt");
		OCREngine enginge = new OCREngine(filters);
		for(int i = 0; i < cards.length;i++) {
			AnalysisResult res = enginge.analyzeImage(new File(cardsPrefix+cards[i]));
			for(int j = 0; j < res.getResultSize();j++) {
				writer.append(res.getWord(j));
				writer.append("\n");
			}
			writer.append("\n");
		}
		writer.close();
	}

}
