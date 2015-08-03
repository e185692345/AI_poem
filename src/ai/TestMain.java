package ai;

import org.json.JSONObject;

import ai.poem.PoemTemplate;
import ai.sentence.MakeSentence;
import ai.word.ChineseWord;
import ai.word.WordPile;

public class TestMain {
	
	public static void main(String[] argv){
		WordPile wordPile = new WordPile("朋友",ChineseWord.noun);
		try {
			wordPile.AddWords(new JSONObject(MainClass.ReadFile("wordPile.json")));
			MakeSentence maker = new MakeSentence(wordPile);
			PoemTemplate poem = PoemTemplate.RandomPoem(8, 5, wordPile, maker);
			System.out.println(poem);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
