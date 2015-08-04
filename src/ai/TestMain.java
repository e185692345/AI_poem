package ai;

import ai.net.ConceptNetCrawler;
import ai.word.ChineseWord;
import ai.word.WordPile;

public class TestMain {
	
	public static void main(String[] argv){
		String topic="狗";
		ConceptNetCrawler wordSource= new ConceptNetCrawler(topic);
		WordPile wordPile;
		/*wordPile= new WordPile(topic, ChineseWord.noun);
		wordPile.AddWords(wordSource.GetWordList_ChineseSource());
		MainClass.WriteToFile("dog_zh.json", wordPile.GetJSONString());*/
		wordPile = new WordPile(topic, ChineseWord.noun);
		wordPile.AddWords(wordSource.GetWordList_EnlishSource());
		MainClass.WriteToFile("dog_en.json", wordPile.GetJSONString());
		
		topic = "朋友";
		/*wordPile= new WordPile(topic, ChineseWord.noun);
		wordPile.AddWords(wordSource.GetWordList_ChineseSource());
		MainClass.WriteToFile("friend_zh.json", wordPile.GetJSONString());*/
		/*wordPile = new WordPile(topic, ChineseWord.noun);
		wordPile.AddWords(wordSource.GetWordList_EnlishSource());
		MainClass.WriteToFile("friend_en.json", wordPile.GetJSONString());*/
	}
}
