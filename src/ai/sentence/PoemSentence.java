package ai.sentence;

import ai.word.ChineseWord;

public class PoemSentence {
	
	private int sentenceType;
	private int[] lineComposition;
	private ChineseWord[] words;
	private int lineCompositionEncode;
	
	public PoemSentence(int sentenceType,ChineseWord[] sentence, int[] lineComposition){
		this.lineComposition = lineComposition;
		lineCompositionEncode = 0;
		for (int element : lineComposition){
			lineCompositionEncode = lineCompositionEncode*10 + element;
		}
		this.setSentence(sentenceType,sentence);
	}
	
	public void setSentence(int sentenceType,ChineseWord[] sentence) {
		this.sentenceType = sentenceType;
		/*不可以用this.words = sentence 這種方式複製*/
		this.words = new ChineseWord[sentence.length];
		for (int i = 0 ; i < sentence.length ; i++){
			words[i] = sentence[i];
		}	
	}
	
	public int getLineCompositionEncode() {
		return this.lineCompositionEncode;
	}
	
	public int[] getLineComposition(){
		return this.lineComposition;
	}
	
	public ChineseWord[] getWords() {
		return words;
	}
	
	public int getSentenceType() {
		return this.sentenceType;
	}
	
	public int getLength() {
		return words.length;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (ChineseWord word : words){
			sb.append(word.getWord()+" ");
		}
		return sb.toString();
	}
}
