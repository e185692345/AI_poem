package ai.poem;

import java.util.HashMap;

import ai.word.ChineseWord;

public class PoemTemplate implements Comparable<PoemTemplate>{
	
	private static final boolean DEBUG = false;
	public final static int scoreRhyme = 200;
	public final static int scoreTone = 200;
	public final static int scoreAntithesis = 100;
	public final static int  scoreDiversity = 100;
	
	private int col, row;
	private int[][] wordComposition;
	private ChineseWord[][] poem = null;
	private int fitnessScore;
	private boolean modified;
	private int maxRhythmMatch, maxToneMatch, maxAntithesisMatch;
	/**
	 * 創建一首新的詩，每首詩可以有不同的模板
	 * <注意>因為poem中的詞語在基因演算法中會被替換，所以每個PoemTemplate都要有一個poem的實體，
	 * 		不可以單純複製reference，否則修改某個PoemTemplate的poem的某個詞的時候會影響到其他人
	 * 
	 * @param row
	 * @param col
	 * @param wordComposition
	 * @param poem 
	 */
	
	public PoemTemplate(int row,int col,int[][] wordComposition, ChineseWord[][] poem){
		this.row = row;
		this.col = col;
		this.wordComposition = wordComposition;
		/*錯誤的複製 : this.poem = poem;*/
		this.poem = new ChineseWord[row][];
		for ( int i = 0 ; i < row ; i++){
			this.poem[i] = new ChineseWord[poem[i].length];
			for ( int j = 0 ; j < poem[i].length ; j++){
				this.poem[i][j] = poem[i][j];
			}
		}
		
		maxRhythmMatch = row/2;
		if (col == 5){
			maxToneMatch = 3*row;
		}
		else{
			maxToneMatch = 4*row;
		}
		maxAntithesisMatch= 0;
		for (int i = 0 ; i < row ; i+= 2){
			maxAntithesisMatch += wordComposition[i].length;
		}
		
		modified = true;
		getFitnessScore();
	}
	
	public ChineseWord[][] getPoem() {
		modified = true;
		return poem;
	}
	
	/**
	 * 當呼叫 getPoem() 系統會認為使用者更改過詩的內容，因此要重新計算 "適應分數"
	 * 否則就直接回傳上次計算完的結果
	 * @return 適應分數
	 */
	public int getFitnessScore() {
		if (modified){
			FitnessFunction();
			modified = false;
		}
		return fitnessScore;
	}

	public int[][] getTeplate(){
		return wordComposition;
	}
	
	private void FitnessFunction(){
		fitnessScore = GetRhythmScore()+GetToneScore()+GetAntithesisScore()+GetDiversityScore();
	}
	
	private int GetDiversityScore(){
		int countUniqueChar = 0;
		HashMap<Character, Boolean> map = new HashMap<Character, Boolean>();
		char c;
		for (int i = 1 ; i <= row*col ; i++){
			c = GetCharAt(i);
			if (!map.containsKey(c)){
				countUniqueChar += 1;
				map.put(c, true);
			}
		}
		
		if (DEBUG) System.out.printf("不重複的字共有 %d / %d 個\n",countUniqueChar,row*col);
		return scoreDiversity*countUniqueChar/(row*col);
	}
	
	private int GetAntithesisScore(){
		int countAntithesis = 0;
		for ( int i = 0 ; i < row ; i += 2){
			for (int j = 0 ; j < wordComposition[i].length ; j++){
				int wordType = poem[i][j].getWordType() & poem[i+1][j].getWordType();
				if ( wordType > 0){
					countAntithesis += 1;
					if (DEBUG) System.out.println(poem[i][j].getWord()+" , "+poem[i+1][j].getWord()+" => "+ ChineseWord.ReadableWordType(wordType));
				}
			}
		}
		if (DEBUG)System.out.printf(">>對偶的詞共有  %d / %d 個\n",countAntithesis,maxAntithesisMatch);
		return countAntithesis*scoreAntithesis/maxAntithesisMatch;
	}
	
	private int GetToneScore(){
		final int[][] standardTone = new int[][]{{0,1,0},{1,0,1},{1,0,1},{0,1,0}};
		int countMatchTone = 0;
		int countMatchRhythmTone = 0;
		int index;
		if (GetToneAt(2) == 0){
			index = 0;  /*平起式*/
		}
		else{
			index = 2;  /*仄起示*/
		}
		
		if (DEBUG) System.out.println("===平仄===");
		for ( int i = 0 ; i < row ; i ++){
			for ( int j = 2, k = 0 ; j <= col ; j+=2, k++){
				int charIndex = i*col+j;
				if (DEBUG) System.out.printf("%c(%d)",GetCharAt(charIndex),GetToneAt(charIndex));
				if (GetToneAt(charIndex) == standardTone[index][k]){
					countMatchTone += 1;
					if (DEBUG) System.out.print("O ");
				}
				else{
					if (DEBUG) System.out.print("X ");
				}
			}
			if (DEBUG)  System.out.println();
			index = (index+1)%4;
		}
		/*處理韻腳的平仄*/
		if (DEBUG) System.out.println("===韻腳平仄===");
		if (poem[0][wordComposition[0].length-1].GetRythm() == poem[1][wordComposition[1].length-1].GetRythm()){
			if (GetToneAt(1*col) == 0){
				countMatchRhythmTone += 1; /*首句押韻用平聲*/
				if (DEBUG) System.out.printf("第1句 : 平 (%c,%c)\n",GetCharAt(col),GetCharAt(2*col));
			}
		}
		else{
			if (GetToneAt(1*col) == 1){
				countMatchRhythmTone += 1; /*首句不押韻用仄聲*/
				if (DEBUG) System.out.println("第1句 : 仄");
			}
		}
		if (GetToneAt(2*col) == 0){
			countMatchRhythmTone += 1;
			if (DEBUG) System.out.println("第2句 : 平");
		}
		for (int i = 4 ; i <= row ; i += 2){
			if ( GetToneAt((i-1)*col) == 1){
				countMatchRhythmTone += 1;
				if (DEBUG) System.out.println("第"+(i-1)+"句 : 仄");
			}
			if ( GetToneAt(i*col) == 0){
				countMatchRhythmTone += 1;
				if (DEBUG) System.out.println("第"+i+"句 : 平");
			}
		}
		if (DEBUG)System.out.printf(">>符合平仄的字有 (%d + %d(韻腳相關)) / %d 個\n",countMatchTone,countMatchRhythmTone,maxToneMatch);
		return (countMatchTone+countMatchRhythmTone)*scoreTone/maxToneMatch;
	}
	/**
	 * 取得整首詩中的某個字
	 * @param index 從"1"開始算
	 * @return 
	 */
	private char GetCharAt(int index){
		if ( index > row*col){
			System.err.println("error : index out of bound");
			System.exit(1);
		}
		if (index <=0){
			System.err.println("error : Index 從 1 開始算");
			System.exit(1);
		}
		index -= 1;
		int atRow = index/col;
		index = index-atRow*col+1;
		int cumulativeSum = 0;
		for ( ChineseWord word : poem[atRow]){
			int len = word.getLength();
			if (cumulativeSum + len >= index){
				return word.getCharAt(index-cumulativeSum-1);
			}
			cumulativeSum += len;
		}
		return '?';
	}
	/**
	 * 取得整首詩中某個字的平仄
	 * @param index 從 "1" 開始算
	 * @return 0:平, 1:仄
	 */
	private int GetToneAt(int index){
		if ( index > row*col){
			System.err.println("error : index out of bound");
			System.exit(1);
		}
		if (index <=0){
			System.err.println("error : Index 從 1 開始算");
			System.exit(1);
		}
		index -= 1;
		int atRow = index/col;
		index = index-atRow*col+1;
		int cumulativeSum = 0;
		for ( ChineseWord word : poem[atRow]){
			int len = word.getLength();
			if (cumulativeSum + len >= index){
				return word.getToneAt(index-cumulativeSum-1);
			}
			cumulativeSum += len;
		}
		return -1;
	}
	
	private int GetRhythmScore(){
		HashMap<Character,Integer> recordRhythm = new HashMap<Character,Integer>();
		int maxCountSameRhytm = 0;
		char mostRhythm='?';
		for (int  i = 1 ; i < row ; i += 2){
			char rhythm = poem[i][wordComposition[i].length-1].GetRythm();
			int temp;
			if (recordRhythm.containsKey(rhythm)){
				temp = recordRhythm.get(rhythm)+1;
			}
			else{
				temp = 1;
			}
			recordRhythm.put(rhythm,temp);
			if ( maxCountSameRhytm < temp){
				maxCountSameRhytm = temp;
				mostRhythm = rhythm;
			}
		}
		if (DEBUG) System.out.printf(">>最多的韻腳是 \"%c\"，共有 %d / %d 個\n",mostRhythm,maxCountSameRhytm,maxRhythmMatch);
		return maxCountSameRhytm*scoreRhyme/maxRhythmMatch;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (ChineseWord[] line : poem){
			for (ChineseWord word : line)
				sb.append(word.getWord()+" ");
			sb.append("\n");
		}
		return sb.toString();
	}

	@Override
	public int compareTo(PoemTemplate other) {
		int score1 = this.getFitnessScore();
		int score2 = other.getFitnessScore();
		if (score1 > score2)
			return -1;
		else if (score1 < score2)
			return 1;
		else {
			return 0;
		}
	}
}
