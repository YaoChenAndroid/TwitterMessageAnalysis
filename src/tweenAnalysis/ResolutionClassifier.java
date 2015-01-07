package tweenAnalysis;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.aliasi.classify.Classification;
import com.aliasi.classify.Classified;
import com.aliasi.classify.ConditionalClassification;
import com.aliasi.classify.DynamicLMClassifier;
import com.aliasi.classify.LMClassifier;
import com.aliasi.corpus.ObjectHandler;
import com.aliasi.util.AbstractExternalizable;
import com.aliasi.util.Compilable;
import com.aliasi.util.Files;

public class ResolutionClassifier {
	private final static String TAG = "Classifier";
	String[] categories;
	LMClassifier temp;
	public String classify(String text) {
	ConditionalClassification classification = temp.classify(text);
	return classification.bestCategory();
	}
	public void Initial() throws ClassNotFoundException, IOException{
		temp= (LMClassifier) AbstractExternalizable.readObject(new File("/home/sunny/Documents/data/classifier"));
		categories = temp.categories();
	}
	public void train() throws IOException, ClassNotFoundException {
		File trainDir;
		trainDir = new File("/home/sunny/Documents/data/trainDirectory");
		categories = trainDir.list();
		int nGram = 7; //the nGram level, any value between 7 and 12 works
		temp= DynamicLMClassifier.createNGramProcess(categories, nGram);

		for (int i = 0; i < categories.length; ++i) {
			String category = categories[i];
			Classification classification = new Classification(category);
			File file = new File(trainDir, categories[i]);
			File[] trainFiles = file.listFiles();
			for (int j = 0; j < trainFiles.length; ++j) {
				File trainFile = trainFiles[j];
				String review = Files.readFromFile(trainFile, "ISO-8859-1");
				Classified classified = new Classified(review, classification);
				((ObjectHandler) temp).handle(classified);  
			}
	 	}
		AbstractExternalizable.compileTo((Compilable) temp, new File("/home/sunny/Documents/data/classifier"));
	}
}