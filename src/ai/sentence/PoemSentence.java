package ai.sentence;

import ai.word.ChineseWord;

public class PoemSentence {
	
	private int sentenceType;
	private ChineseWord[] words;
	
	public PoemSentence(int sentenceType) {
		this.sentenceType = sentenceType;
		words = new ChineseWord[0];
	}
	
	public PoemSentence(int sentenceType,ChineseWord[] sentence){
		words = new ChineseWord[0];
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
	
	public ChineseWord[] getWords() {
		return words;
	}
	
	public int getSentenceType() {
		return this.sentenceType;
	}
	
	public int length() {
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
