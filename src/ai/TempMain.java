package ai;

import org.json.JSONException;
import org.json.JSONObject;

import ai.exception.MakeSentenceException;
import ai.net.ConceptNetCrawler;
import ai.sentence.MakeSentence;
import ai.word.ChineseWord;
import ai.word.WordPile;

public class TempMain {
	
	public static void main(String[] argv){
		String topic = "狗";
		WordPile wordPile = new WordPile(topic, ChineseWord.NOUN);
		ConceptNetCrawler wordSource= new ConceptNetCrawler(topic);
		//wordPile.AddWords(wordSource.getWordList_ChineseSource());
		try {
			wordPile.addWords(new JSONObject(MainClass.ReadFile("wordPile.json")));
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		MakeSentence maker = new MakeSentence(wordPile);
		MainClass.WriteToFile("wordPile.json", wordPile.getJSONString());
		for (int i = 0 ; i < 46 ; i++)
			try {
				System.out.println(i+" : "+maker.makeSentence(i));
			} catch (MakeSentenceException e) {
				System.err.println(i+" : "+e.getMessage());
			}
		
	}
}
