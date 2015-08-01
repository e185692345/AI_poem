package ai;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import ai.GeneticAlgorithm.GeneticAlgorithm;
import ai.net.JSONReader;
import ai.sentence.LineComposition;
import ai.sentence.MakeSentence;
import ai.word.WordPile;

public class TestMain extends MainClass{
	public static void main(String[] args){
		String str="<td colspan=\"2\">&nbsp;1.　毫<span class=\"key\">不做作</span></td>\n</tr>";
		Matcher matcher3 = Pattern.compile("1\\.　.*<span class=\"key\">.*<\\/span>.*<\\/td>").matcher(str);
		matcher3.find();
		String str1 = matcher3.group();
		str1 = str1.substring(3).replaceAll("<.*?>","");
		System.out.println(str1);
	}
}
