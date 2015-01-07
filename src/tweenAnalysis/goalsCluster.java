package tweenAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class goalsCluster {
	private HashMap<String, Integer> dic;
	private Element rootElement;
	private ArrayList<Node> related, unRelated;
	private HashMap<String, Integer> divider;
	private int dividerCount;
	private float totelMessage;
	private float tfDivider;
	private ArrayList<ArrayList<Node>> result;
	public goalsCluster() throws IOException, SAXException, ParserConfigurationException{
		loadfile();
	}
	private void loadfile() throws IOException, SAXException, ParserConfigurationException {
		// TODO Auto-generated method stub
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/home/sunny/Documents/data/wordFrequency")));
		String data = null;
		dic = new HashMap();
		String[] temp;
		while((data = br.readLine())!=null)
		{
			temp = data.split("=");
			dic.put(temp[0], Integer.valueOf(temp[1]));
		}
		File fXmlFile = new File("/home/sunny/Documents/data/yaochen.xml");
		long tem = fXmlFile.length();
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document docTemp = dBuilder.parse(fXmlFile);
			docTemp.getDocumentElement().normalize();
			rootElement = docTemp.getDocumentElement();

	}
	public void classsifyGoals() throws FileNotFoundException{
		NodeList all = rootElement.getChildNodes();
		totelMessage = all.getLength();
		related = new ArrayList();
		unRelated = new ArrayList();
		ArrayList<Node> source = convertToArray(all);
		float d = (float)0.1;
		DivideTo2(source, d);
		result = new ArrayList();
		result.add((ArrayList)related.clone());
		result.add((ArrayList)unRelated.clone());
		int power;
		for(int i = 1; i < 2; i++){
			power = (int) Math.pow(2, i);
			for(int j = 0; j < power; j++){
				DivideTo2(result.get(0), d);
				result.remove(0);
				result.add((ArrayList)related.clone());
				result.add((ArrayList)unRelated.clone());
			}
		}
		writeToDist();
	}
	private ArrayList<Node> convertToArray(NodeList list)
	  {
	      int length = list.getLength();
	      ArrayList<Node> copy = new ArrayList();
	      for (int n = 0; n < length; ++n)
	      	copy.add(list.item(n));
	      return copy;
	  }
	//term frequency–inverse document frequency
	private void DivideTo2(ArrayList<Node> source, double d){
		related.clear();
		unRelated.clear();
		if(source.size() == 0)
			return;

		 NodeList temp = source.get(0).getChildNodes();
		 Node curNode;
		 String dividerSource = temp.item(0).getChildNodes().item(0).getNodeValue();
		 String dividerStr = temp.item(5).getChildNodes().item(0).getNodeValue();
		 divider = new HashMap();
		String[] divArray = dividerStr.split(" ");
		String[] sourceD = dividerSource.split(" ");
		dividerCount = sourceD.length;
		tfDivider = (float)1.0/dividerCount;
		for(int i = 0, nLen = divArray.length; i < nLen; i++){
			divider.put(divArray[i], 1);
		}
		 String tester, testerSource;
		 float result; 
		 for(int i = 1, nLen = source.size(); i < nLen; i++){
			 curNode = source.get(i);
			 temp = curNode.getChildNodes();
			 testerSource = temp.item(0).getChildNodes().item(0).getNodeValue();
			 tester = temp.item(5).getChildNodes().item(0).getNodeValue();
			 result = computeSim(tester, testerSource);
			 System.out.println(result);
			 if(result > d){
				 related.add(curNode);
			 }else
				 unRelated.add(curNode);
		 }
		 related.add(source.get(0));
	}
	private float computeSim(String tester, String testerSource){
		float result = 0;

		String[] testerD = testerSource.split(" ");
		int testerCount = testerD.length;
		String[] common = getCommon(tester);
		int nLen = common.length;
		float dividerVector,testerVector;
		float idf;
		float tf = (float)1.0/testerCount;
		for(int i = 0; i < nLen; i++){
			if(common[i] == null)
				break;
			idf = (float)Math.log(this.totelMessage / dic.get(common[i]));
			dividerVector = tfDivider*idf;
			testerVector = tf*idf;
			result = result + dividerVector * testerVector;
		}		
		return result*10;
	}
	private String[] getCommon(String tester) {
		// TODO Auto-generated method stub
		String[] testerArray = tester.split(" ");
		int  nLen = testerArray.length;
		String[] res = new String[nLen];
		String key;
		int nCount = 0;
		for(int i = 0; i < nLen; i++){
			key = testerArray[i];
			if(divider.containsKey(key)){
				res[nCount] = key;
				nCount++;
			}
		}
		return res;
	}
	private void writeToDist() throws FileNotFoundException{
		ArrayList<Node> temp;
		String goal;
		PrintWriter pw;
		int nLenght;
		for(int i = 0, nLen = result.size(); i < nLen; i++){
			temp = result.get(i);
			nLenght = temp.size();
			if(nLenght > 0){
				pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("/home/sunny/Documents/data/goalClassiferTrain/" + String.valueOf(i))),true);
				for(int j = 0; j < nLenght; j++){
					goal = temp.get(j).getChildNodes().item(5).getChildNodes().item(0).getNodeValue();
					pw.println(goal);
				}
				pw.close();
			}
		}
	}
}
