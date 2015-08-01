package ai;

import java.util.Arrays;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

import Concept.Main;
import ai.net.JSONReader;
import ai.net.MicrosoftTranslatorKey;
import ai.word.ChineseWord;
import ai.word.WordPile;

public class TranslateMain extends MainClass{
	
	public static void main(String[] argv){
		Translate.setClientId(MicrosoftTranslatorKey.ID);
		Translate.setClientSecret(MicrosoftTranslatorKey.SECRET);
		
		ChineseWord[] wordList = new JSONReader("朋友").GetWordList_UsingEnlishSource();
		for (ChineseWord word : wordList)
			System.out.println(word.toString());
	}
}
