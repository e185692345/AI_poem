package ai;

import org.json.JSONObject;

import ai.sentence.MakeSentence;
import ai.word.ChineseWord;
import ai.word.WordPile;

public class TestMain {
	
	public static void main(String[] argv){
		WordPile wordPile = new WordPile("朋友",ChineseWord.noun);
		try {
			wordPile.AddWords(new JSONObject(MainClass.ReadFile("wordPile.json")));
			MakeSentence maker = new MakeSentence(wordPile);
			for (int i = 0 ; i < 10 ; i++)
				System.out.println(maker.makeSentence(MakeSentence.sentenceType2));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
