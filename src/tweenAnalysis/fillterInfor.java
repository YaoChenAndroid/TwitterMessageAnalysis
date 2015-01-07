package tweenAnalysis;

public class fillterInfor {
	private final static String TAG = "fillterInfor";
	public String removeUselessInfo(String source){
		String res = source.toLowerCase();
		res = res.replaceAll("((www/.[\\s]+)|(https?://[^\\s]+))|(\\S*\\.\\S+\\.\\S+)|#|@|(:-\\))|(:-\\()|\\.|\\,", "");   
		res = res.replaceAll("[“”’‘'']", "\"");
		res = res.replace("\"", " \" ");
		res = res.replace(":", " : ");
		res = res.replace("!", " ! ");
		res = res.replace("?", " ? ");
		res = res.trim();
		return res;
	}
}
