package tweenAnalysis;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class wordDictionary {
	private static wordDictionary curDic = null;
	HashMap<String, Integer> dic;
	private int totalDoc;
	HashMap<String, Integer> dicFrequence;
	HashMap<String, Integer> currentDoc;
	protected wordDictionary(){
		dic = new HashMap();
		currentDoc = new HashMap();
		dicFrequence = new HashMap();
		totalDoc = 0;
	}
	public static wordDictionary GetInstance(){
		if(curDic == null)
			curDic = new wordDictionary();
		return curDic;
	}
	public void writeToMem(String source){
		totalDoc++;
		currentDoc.clear();
		String[] words = source.split(" ");
		String key;
		String[] wordClassify;
		int value;
		for(int i = 0, nLen = words.length; i < nLen; i++){
				key = words[i];
				if(key.isEmpty())
					continue;
				if(dic.containsKey(key))
				{
					value = dic.get(key)+ 1;
				}
				else
					value = 1;
				dic.put(key, value);
				if(!currentDoc.containsKey(key)){
					if(dicFrequence.containsKey(key)){
						value = dicFrequence.get(key) + 1;
					}
					else
						value = 1;
					dicFrequence.put(key, value);
				}
				currentDoc.put(key, 1);
		}
	}
	public void writeToDisk() throws FileNotFoundException{

		PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("/home/sunny/Documents/data/wordFrequency")),true);
		String temp;
		for( Map.Entry<String, Integer> entry : dic.entrySet()) {
			temp = entry.toString();
			pw.println(temp);

		}
		pw.close();
		PrintWriter frequence = new PrintWriter(new OutputStreamWriter(new FileOutputStream("/home/sunny/Documents/data/wordIndex")),true);
		temp = "";

		for( Map.Entry<String, Integer> entry : dicFrequence.entrySet()) {
			temp = entry.toString();
			frequence.println(temp);

		}
		frequence.close();
		dic.clear();
		dicFrequence.clear();
	}
	public int[] GetScore(String[] source){
		int[] res = new int[5];
		res[0] = (int)(getPartScore(source[0]) * 0.2);
		res[1] = getPartScore(source[1]);
		res[2] = getPartScore(source[2]) * 2;
		res[3] = (int)(getPartScore(source[3]) * 0.2);
		res[4] = (int)(getPartScore(source[4]) * 0.2);;
		return res;
	}
	private int getPartScore(String source) {
		// TODO Auto-generated method stub
		String[] word = source.split(" ");
		int res = 0;
		for(int i = 0, nLen = word.length; i < nLen; i++){
			if(dic.containsKey(word[i]))
				res = res + dic.get(word[i]);
		}
		return res;
	}

}
