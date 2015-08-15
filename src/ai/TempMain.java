package ai;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.sun.org.apache.xpath.internal.operations.Bool;

import ai.GeneticAlgorithm.GeneticAlgorithm;
import ai.exception.MakeSentenceException;
import ai.net.ConceptNetCrawler;
import ai.sentence.MakeSentence;
import ai.word.ChineseWord;
import ai.word.WordPile;

public class TempMain {
	
	public static void main(String[] argv){
		String topic = "跑步";
		JSONObject obj = null;
		
		
		try {
			obj = new JSONObject(MainClass.ReadFile("run3.json"));
		} catch (JSONException e2) {
			e2.printStackTrace();
			System.exit(1);
		}
		WordPile wordPile = new WordPile(topic, ChineseWord.NOUN);
		ConceptNetCrawler wordSource= new ConceptNetCrawler(topic);
		//wordPile.AddWords(wordSource.getWordList_ChineseSource(obj));
		try {
			wordPile.addWords(new JSONObject(MainClass.ReadFile("wordPile.json")));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		MakeSentence maker = new MakeSentence(wordPile);
		MainClass.WriteToFile("wordPile.json", wordPile.getJSONString());
		
		for (int i = 0 ; i < maker.getCountType() ; i++){
			HashMap<String,Boolean> countSentence = new HashMap<String,Boolean>();
			int count = 0;
			for (int j = 0 ; j < 100 ; j++){
				String sentence;
				try {
					sentence = maker.makeSentence(i).toString();
					if ( !countSentence.containsKey(sentence)){
						countSentence.put(sentence, true);
						count += 1;
					}
				} catch (MakeSentenceException e) {
					break;
				}
			}
			if (count > 0)
				System.out.println(i+" : "+count);
		}
		new GeneticAlgorithm(8, 5, wordPile, maker).evole();
	}
}
