package ai;

import org.json.JSONException;
import org.json.JSONObject;

import ai.GeneticAlgorithm.GeneticAlgorithm;
import ai.net.JSONReader;
import ai.sentence.LineComposition;
import ai.sentence.MakeSentence;
import ai.word.WordPile;

public class TestMain extends MainClass{
	public static void main(String[] args){
		WordPile wordPile = null;
		/*=====================================*/
		/*選擇字詞來源 NET_SOURCE(從conceptnet) 或 FILE_SOURCE(wordPile.json)*/
		final int SOURCE = FILE_SOURCE;
		/*如果來源是NET_SOURCE則要指定主題*/
		final String topic = new String("狗");
		/*=====================================*/
		
		switch (SOURCE){
		case NET_SOURCE :
			wordPile = new WordPile(JSONReader.GetWordList(topic));
			WriteToFile("wordPile.json", wordPile.GetJSONString());
			break;
		case FILE_SOURCE:
			try {
				wordPile = new WordPile(new JSONObject(ReadFile("wordPile.json")));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:
			System.err.println("error Incorrect source");
			System.exit(1);
		}
		
		MakeSentence make = new MakeSentence(wordPile);
		for (int i = 0 ; i < 10 ; ){
			String sentence = make.GetSenTenceType1(LineComposition.GetRandomComposition(5));
			if (sentence == null){
				//System.out.println("null");
			}
			else{
				System.out.println(sentence);
				i+=1;
			}
		}
		
	}
}
