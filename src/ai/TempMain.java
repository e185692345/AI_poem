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
			WordPile wordPile = new WordPile("狗", ChineseWord.NOUN);
			wordPile.addWords(new JSONObject(MainClass.ReadFile("wordPile.json")));
			MakeSentence maker = new MakeSentence(wordPile);
			for (int i = 0 ; i < 100 ; i++)
				try {
					System.out.println(maker.makeSentence(LineComposition.getRandomComposition(5)));
				} catch (MakeSentenceException e) {
					System.err.println(e.getMessage());
					continue;
				}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
}
