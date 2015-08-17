package ai;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import ai.GeneticAlgorithm.GeneticAlgorithm;
import ai.GeneticAlgorithm.MyRandom;
import ai.exception.MakeSentenceException;
import ai.sentence.LineComposition;
import ai.sentence.MakeSentence;
import ai.word.ChineseWord;
import ai.word.WordPile;

public class TestMain {
	
	public static void main(String[] argv){
		WordPile wordPile = new WordPile("狗", ChineseWord.NOUN);
		new MakeSentence(wordPile);
	}
}
