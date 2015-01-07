package tweenAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class GoalsExtract {
	public static GoalsExtract reader= null;
	private Element rootElement;
	private HashMap<String, Integer> dic;
	private Element rootResult;
	private Document doc;
	private GoalsExtract() throws ParserConfigurationException, IOException, SAXException{
		loadfile();		
		DocumentBuilderFactory docFactory;
		DocumentBuilder docBuilder;
		docFactory = DocumentBuilderFactory.newInstance();
		docBuilder = docFactory.newDocumentBuilder();
		doc = docBuilder.newDocument();
		rootResult = doc.createElement("tweeter");
		doc.appendChild(rootResult);
	}
	public static GoalsExtract GetInstance() throws ParserConfigurationException, IOException, SAXException{
		if(reader == null)
			reader = new GoalsExtract();
		return reader;
	}
	private void loadfile() throws IOException, SAXException, ParserConfigurationException {
		// TODO Auto-generated method stub
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/home/sunny/Documents/data/Dictionary")));
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
	public void match(String rawSentence, String source){
		NodeList message = rootElement.getChildNodes();
		NodeList parts;
		String newResolution, midPart;
		String[] LeftResolutionRest = new String[3];
		String[] MidRest = new String[2];
		String[] sourceParts = new String[4];
		int curScore = 0;
		int maxScore = 0;
		Node currentNode = null;
		String[] currentDivide = new String[4];
		for(int i = 0, nLen = message.getLength(); i < nLen; i++){
			parts = message.item(i).getChildNodes();
			newResolution = parts.item(3).getChildNodes().item(0).getNodeValue();
			midPart = parts.item(4).getChildNodes().item(0).getNodeValue();
			LeftResolutionRest = matchResolution(source, newResolution);
			if(LeftResolutionRest == null)
				continue;
			String orient = ((Element)message.item(i)).getAttribute("orientation");
			if(orient.compareTo("true") == 0){
				MidRest = matchMid(LeftResolutionRest[2], midPart,false);
				sourceParts[0] = LeftResolutionRest[0];
				sourceParts[1] = LeftResolutionRest[1];
				sourceParts[2] = MidRest[0];//mid part
				sourceParts[3] = MidRest[1];// goal +right part
			}
			else{
				MidRest = matchMid(LeftResolutionRest[0], midPart,false);
				sourceParts[0] = MidRest[1];//left part + goal
				sourceParts[1] = LeftResolutionRest[1];
				sourceParts[2] = MidRest[0];
				sourceParts[3] = LeftResolutionRest[2];
			}			

			curScore = computeScore(parts, sourceParts);
			if(curScore > maxScore){
				maxScore = curScore;
				currentNode = message.item(i);
				currentDivide = sourceParts.clone();
			}
		}
		AddNode(currentNode,currentDivide,rawSentence,source);
	}
	private void AddNode(Node currentNode, String[] currentDivide, String rawSentence, String source) {
		// TODO Auto-generated method stub
		try{
		Element result = doc.createElement("result");
		rootResult.appendChild(result);
		Node newNode = currentNode.cloneNode(true);
		doc.adoptNode(newNode);
		result.appendChild(newNode);
		
		Element test = doc.createElement("testMessage");
		result.appendChild(test);
		
		Element raw = doc.createElement("raw");
		raw.appendChild(doc.createTextNode(rawSentence));
		test.appendChild(raw);
		Element sourceN = doc.createElement("source");
		sourceN.appendChild(doc.createTextNode(source));
		test.appendChild(sourceN);
		
		Element left = doc.createElement("left");
		left.appendChild(doc.createTextNode(currentDivide[0]));
		test.appendChild(left);
		Element resolution = doc.createElement("resolution");
		resolution.appendChild(doc.createTextNode(currentDivide[1]));
		test.appendChild(resolution);
		Element mid = doc.createElement("mid");
		mid.appendChild(doc.createTextNode(currentDivide[2]));
		test.appendChild(mid);
		Element rest = doc.createElement("rest");
		rest.appendChild(doc.createTextNode(currentDivide[3]));
		test.appendChild(rest);
		}catch(Exception e){
			System.out.print(e.getMessage());
		}
	}
	private int computeScore(NodeList parts, String[] source) {
		// TODO Auto-generated method stub
		int[] partScore = new int[4];
		partScore[0] = Integer.parseInt(((Element)parts.item(2)).getAttribute("score"));
		partScore[1] = Integer.parseInt(((Element)parts.item(3)).getAttribute("score"));
		partScore[2] = Integer.parseInt(((Element)parts.item(4)).getAttribute("score"));
		partScore[3] = Integer.parseInt(((Element)parts.item(5)).getAttribute("score")) +  Integer.parseInt(((Element)parts.item(6)).getAttribute("score"));;
		int[] res = new int[4];
		res[0] = (int)(getPartScore(source[0]) * 0.2);
		res[1] = getPartScore(source[1]);
 		res[2] = getPartScore(source[2]) * 2;
		res[3] = (int)(getPartScore(source[3]) * 0.2);
		int result = 0;
		for(int i = 0; i < 4; i++){
			result = result + partScore[i] * res[i];
		}
		return result;
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
	private String[] matchMid(String source, String midPart, boolean LRflag) {
		// TODO Auto-generated method stub
		String[] common = comSubstring(source, midPart);
		StringBuffer tempSource = new StringBuffer();
		for(int i = 0, nLen = common.length; i < nLen; i++){
			tempSource.append(common[i] + " ");
		}
		String[] resoluCommon = comSubstring(tempSource.toString(), midPart);

		String[] result = new String[2];
		if(resoluCommon.length == 0)
		{
			result[0] = "";
			result[1] = source;
		}
		else if(LRflag){
			String lastMidWord = resoluCommon[resoluCommon.length - 1];
			int midEnd = source.indexOf(lastMidWord) + lastMidWord.length();

			result[0] = source.substring(0, midEnd);
			result[1] = source.substring(midEnd);					
		}
		else{
			int midBegin = source.lastIndexOf(resoluCommon[0]);
			result[0] = source.substring(midBegin);
			result[1] = source.substring(0, midBegin);
		}
		return result;
	}
	private  String[] comSubstring(String str1, String str2) {
		String[] strsource = str1.split(" ");
		String[] key = str2.split(" ");
		int sourceL = strsource.length;
		int keyL = key.length;
		int[][] lcs = new int[sourceL + 1][keyL + 1];
		// 初始化数组
		for (int i = 0; i <= keyL; i++) {
			for (int j = 0; j <= sourceL; j++) {
				lcs[j][i] = 0;
			}
		}
		for (int i = 1; i <= sourceL; i++) {
			for (int j = 1; j <= keyL; j++) {
				if (strsource[i - 1].compareTo(key[j - 1]) == 0) {
					lcs[i][j] = lcs[i - 1][j - 1] + 1;
				}
				if (strsource[i - 1].compareTo(key[j - 1]) != 0) {
					lcs[i][j] = lcs[i][j - 1] > lcs[i - 1][j] ? lcs[i][j - 1]
							: lcs[i - 1][j];
				}
			}
		}
		// 输出数组结果进行观察
//		for (int i = 0; i <= sourceL; i++) {
//			for (int j = 0; j <= keyL; j++) {
//				System.out.print(lcs[i][j]+",");
//			}
//			System.out.println("");
//		}
		// 由数组构造最小公共字符串
		int max_length = lcs[sourceL][keyL];
		String[] comStr = new String[max_length];
		int i =sourceL, j =keyL;
		while(max_length>0){
			if(lcs[i][j]!=lcs[i-1][j-1]){
				if(lcs[i-1][j]==lcs[i][j-1]){//两字符相等，为公共字符
					comStr[max_length-1]=strsource[i-1];
					max_length--;
					i--;j--;
				}else{//取两者中较长者作为A和B的最长公共子序列
					if(lcs[i-1][j]>lcs[i][j-1]){
						i--;
					}else{
						j--;
					}
				}
			}else{
				i--;j--;
			}
		}
		return comStr;
	}
	private String[] matchResolution(String source, String newResolution) {
		// TODO Auto-generated method stub
		String[] common = comSubstring(source, newResolution);
		StringBuffer tempSource = new StringBuffer();
		for(int i = 0, nLen = common.length; i < nLen; i++){
			tempSource.append(common[i] + " ");
		}
		String[] resoluCommon = comSubstring(tempSource.toString(), newResolution);
		if(resoluCommon == null)
			return null;
		int leftEnd = source.indexOf(resoluCommon[0]);
		String lastResoluWord = resoluCommon[resoluCommon.length - 1];
		int resoluEnd = source.indexOf(lastResoluWord);
		int restBegin = resoluEnd + lastResoluWord.length();
		String[] res = new String[3];
		res[0] = source.substring(0, leftEnd);
		res[1] = source.substring(leftEnd, restBegin);
		res[2] = source.substring(restBegin);
		return res;				
	}
	public void writeResult() throws TransformerException{

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File("/home/sunny/Documents/data/result.xml"));
 
		// Output to console for testing
		// StreamResult result = new StreamResult(System.out); 
		transformer.transform(source, result); 
	}
}
