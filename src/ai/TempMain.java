package ai;

import org.json.JSONException;
import org.json.JSONObject;

import ai.exception.MakeSentenceException;
import ai.sentence.LineComposition;
import ai.sentence.MakeSentence;
import ai.word.ChineseWord;
import ai.word.WordPile;

public class TempMain {
	
	public static void main(String[] argv){
		
		try {
			WordPile wordPile = new WordPile("朋友", ChineseWord.NOUN);
			wordPile.addWords(new JSONObject(MainClass.ReadFile("friend_en.json")));
			wordPile.addWords(new JSONObject(MainClass.ReadFile("friend_zh.json")));
			MainClass.WriteToFile("friend_all.json", wordPile.getJSONString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
}
