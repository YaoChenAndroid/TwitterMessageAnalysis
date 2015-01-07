package tweenAnalysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

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

public class XMLWrite {
	private final static String TAG = "XMLRW";
	private DocumentBuilderFactory docFactory;
	private DocumentBuilder docBuilder;
	private Document doc;
	private Element rootElement;
	private wordDictionary dic;
	public XMLWrite() throws ParserConfigurationException{
		docFactory = DocumentBuilderFactory.newInstance();
		docBuilder = docFactory.newDocumentBuilder();
		// root elements
		doc = docBuilder.newDocument();
		rootElement = doc.createElement("tweeter");
		doc.appendChild(rootElement);
		dic = wordDictionary.GetInstance();
	}
	public void writenode(String source, String all, String resolution, String key){
		//write dictionary
		dic.writeToMem(all);
		// staff elements
		Element message = doc.createElement("message");
		rootElement.appendChild(message);
		ArrayList<String> part = new ArrayList();
		Boolean orientation= getArray(source, all, resolution, key, part);
		// shorten way
		message.setAttribute("orientation", String.valueOf(orientation));
		// firstname elements	
		Element sourceE = doc.createElement("source");
		sourceE.appendChild(doc.createTextNode(source));
		message.appendChild(sourceE);
		
		Element resE = doc.createElement("res");
		resE.appendChild(doc.createTextNode(all));
		message.appendChild(resE);
		
		Element left = doc.createElement("left");
		left.appendChild(doc.createTextNode(part.get(0)));
		message.appendChild(left);
		Element resolu = doc.createElement("resolu");
		resolu.appendChild(doc.createTextNode(part.get(1)));
		message.appendChild(resolu);
		Element mid = doc.createElement("mid");
		mid.appendChild(doc.createTextNode(part.get(2)));
		message.appendChild(mid);
		Element keyElm = doc.createElement("key");
		keyElm.appendChild(doc.createTextNode(part.get(3)));
		message.appendChild(keyElm);
		Element right = doc.createElement("right");
		right.appendChild(doc.createTextNode(part.get(4)));
		message.appendChild(right);
		
	}
	
	private Boolean getArray(String source, String all, String resolution, String key, ArrayList part) {
		// TODO Auto-generated method stub 

		int nSmall = all.indexOf(resolution);		
		if(nSmall == -1)
		{
			System.out.println("111"+ resolution + "111");
		}
		int nBig = all.indexOf(key);
		if(nBig == -1)
		{
			System.out.println(all);
			System.out.println("222"+ key + "222");
		}
		boolean bOrien = true;
		if(nSmall > nBig)
		{
			int nTemp = nBig;
			nBig = nSmall;
			nSmall = nTemp;		
			bOrien = false;
		}
		String left = all.substring(0, nSmall);
		String mid, right;
		if(bOrien){
			mid = all.substring(nSmall + resolution.length(), nBig);
			right = all.substring(nBig + key.length());

		}else{
			mid = all.substring(nSmall + key.length(), nBig);
			right = all.substring(nBig + resolution.length());

		}

		part.add(left);
		part.add(resolution);
		part.add(mid);
		part.add(key);
		part.add(right);
		return bOrien;
		
	}
	private void updateVector(){
		NodeList messageNodes = rootElement.getChildNodes();
		NodeList sentencePart;
		
		int[] res;
		String[] parts = new String[5];
		for(int i = 0, nLen = messageNodes.getLength(); i < nLen; i++)
		{
			sentencePart = messageNodes.item(i).getChildNodes();
			for(int j = 0; j < 5; j++){
				parts[j] = sentencePart.item(j+2).getChildNodes().item(0).getNodeValue();
			}				
			res = dic.GetScore(parts);
			for(int j = 2, nPLen = sentencePart.getLength(); j < nPLen; j++){
				((Element)sentencePart.item(j)).setAttribute("score", String.valueOf(res[j-2]));
			}
		}
	}
	public void writeXML() throws TransformerException, FileNotFoundException
	{
		updateVector();
    	dic.writeToDisk();
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File("/home/sunny/Documents/data/yaochen.xml"));
 
		// Output to console for testing
		// StreamResult result = new StreamResult(System.out); 
		transformer.transform(source, result); 
		System.out.println("File saved!");
	}
}
