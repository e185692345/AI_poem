package ai.sentence;

import ai.word.ChineseWord;
import ai.word.Relation;
import ai.word.WordPile;

public class MakeSentence {
	public static final int sentenceType1 = 0;	// adj(HasProperty) + 的 + noun(IsA)
	
	private WordPile wordPile;
	public MakeSentence(WordPile wordPile) {
		this.wordPile = wordPile;
	}
	
	public String GetSenTenceType1(int[] lineCompositiom){
		if (lineCompositiom.length == 2){
			ChineseWord adj = wordPile.GetRlationWord(Relation.HasProperty, Relation.END, lineCompositiom[0]);
			ChineseWord noun = wordPile.GetRlationWord(Relation.IsA, Relation.END, lineCompositiom[1]);
			if (adj == null || noun == null){
				return null;
			}
			else{
				return adj.getWord()+noun.getWord();
			}
		}
		else if (lineCompositiom.length == 3 && lineCompositiom[1] == 1){
			ChineseWord adj = wordPile.GetRlationWord(Relation.HasProperty, Relation.END, lineCompositiom[0]);
			ChineseWord noun = wordPile.GetRlationWord(Relation.IsA, Relation.END, lineCompositiom[2]);
			if (adj == null || noun == null){
				return null;
			}
			else{
				return adj.getWord()+"的"+noun.getWord();
			}
		}
		else{
			return null;
		}
	}
}
