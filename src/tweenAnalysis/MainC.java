package tweenAnalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

public class MainC {
	private final static String TAG = "MainC";
	public ResolutionClassifier resClassifier;
	public static void main(String argv[]) throws ParserConfigurationException, TransformerException, ClassNotFoundException, IOException, SAXException {	
		MainC main = new MainC();
		main.generateXML();
//		main.resClassifier = new Classifier();
		
//		main.resClassifier.train();
//		main.generateXML();
//		String source = "I don't want to jinx it, but I think my New Year's resolution is finally working. As of today -46 lbs.";
//		String source = "I knew it! Zombies threat is real and I am glad my new year resolution is all about preparing for them.....:)... http://fb.me/6ubE0nWS0 ";
//		main.match(source);
		goalsCluster classifier = new goalsCluster();
		classifier.classsifyGoals();
		System.out.print("end");
	}
	private void train() throws ClassNotFoundException, IOException{
		ResolutionClassifier resClassifier = new ResolutionClassifier();
		resClassifier.train();
	}
	private void classifyResolution() throws IOException, ClassNotFoundException{
		resClassifier.Initial();
        FileReader fr = new FileReader("/home/sunny/Documents/data/source");
        BufferedReader br = new BufferedReader(fr);
		String source = br.readLine().trim();
		
        FileWriter fwRes = new FileWriter("/home/sunny/Documents/data/classRes/res");
        BufferedWriter bwRes = new BufferedWriter(fwRes);    
        FileWriter fwNot = new FileWriter("/home/sunny/Documents/data/classRes/not");
        BufferedWriter bwNot = new BufferedWriter(fwNot);    
        while (source.compareTo("") != 0) {
        	if(resClassifier.classify(source).compareTo("res") == 0)
        	{
        		bwRes.write(source); 
        		bwRes.newLine();
        	}else{
        		bwNot.write(source); 
        		bwNot.newLine();
        	}
        	source = br.readLine().trim();
        }
        bwRes.flush();    
        bwRes.close();
        fwRes.close();
        bwNot.flush();    
        bwNot.close();
        fwNot.close();
	}
	private void match(String source) throws ParserConfigurationException, IOException, SAXException, TransformerException{
		GoalsExtract matcher = GoalsExtract.GetInstance();
		fillterInfor inforF = new fillterInfor();
		stem stemtool = new stem();
		String filtedStr = inforF.removeUselessInfo(source);
		filtedStr = stemtool.getStem(filtedStr);
		matcher.match(source, filtedStr);
		matcher.writeResult();
	}
	
	
	private void generateXML() throws ParserConfigurationException, IOException, TransformerException{
        FileReader fr = new FileReader("/home/sunny/Documents/data/resolution");
        BufferedReader br = new BufferedReader(fr);    
        FileReader frkey = new FileReader("/home/sunny/Documents/data/resolutionKey");
        BufferedReader brkey = new BufferedReader(frkey);    
        String myreadline, source,resolu, key, res;    
        String[] temp;
		fillterInfor inforF = new fillterInfor();
		stem stemtool = new stem();
		XMLWrite xmltool = new XMLWrite();
		source = br.readLine();
        while (source.trim().compareTo("") != 0) {	        	
	        	myreadline = brkey.readLine();
	        	temp = myreadline.split("\t");
	        	resolu = temp[0];
	        	key = temp[1];
				res = inforF.removeUselessInfo(source);
				resolu = inforF.removeUselessInfo(resolu);
				key = inforF.removeUselessInfo(key);
				res = stemtool.getStem(res);
				resolu = stemtool.getStem(resolu);
				key = stemtool.getStem(key);
				xmltool.writenode(source, res ,resolu, key);
				source = br.readLine();
            }

        	xmltool.writeXML();
            br.close();
            fr.close();
	}
}
