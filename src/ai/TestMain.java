package ai;

import org.json.JSONException;
import org.json.JSONObject;

import ai.GeneticAlgorithm.GeneticAlgorithm;
import ai.sentence.MakeSentence;
import ai.word.ChineseWord;
import ai.word.WordPile;

public class TestMain {
	
	public static void main(String[] argv){
		WordPile wordPile;
		//==========================================================================
		// 主題 :狗 
		//String topic="狗";
		//String[] fileList = {"dog_zh.json","dog_en.json","dog_all.json"};
		//==========================================================================
		// 主題 : 朋友 
		String topic="朋友";
		String[] fileList = {"friend_zh.json","friend_en.json","friend_all.json"};
		//==========================================================================
		for ( String file : fileList){
			try {
				wordPile= new WordPile(topic, ChineseWord.NOUN);
				wordPile.addWords(new JSONObject(MainClass.ReadFile(file)));
				MakeSentence maker = new MakeSentence(wordPile);
				for ( int i = 0 ; i < 1 ; i++){
					new GeneticAlgorithm(8, 5, wordPile, maker).evole();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
