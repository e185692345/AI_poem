package ai.poem;

import java.util.HashMap;

import ai.exception.MakeSentenceException;
import ai.sentence.MakeSentence;
import ai.sentence.PoemSentence;
import ai.word.ChineseWord;
import ai.word.WordPile;

public class PoemTemplate implements Comparable<PoemTemplate>{
	
	private static final boolean DEBUG = false;
	// TODO 新增分數種類時也要一並更改這個數值
	public final static int COUNT_FITNESS_TYPE = 4;
	private int[] detailScore = new int[COUNT_FITNESS_TYPE];
	public final static int MAX_RHYTHM_SCORE = 100;
	public final static int MAX_TONE_SCORE = 200;
	public final static int MAX_ANTITHESIS_SCORE = 200;
	public final static int MAX_DIVERSITY_SCORE = 100;
	
	private int col, row;
	private PoemSentence[] poem;
	private int fitnessScore;
	private boolean modified;
	//int rhythmScore, toneScore, antithesisScore, diversityScore;
	private int maxRhythmMatch, maxToneMatch, maxAntithesisMatch, maxDiversityMatch;
	/**
	 * 創建一首新的詩，每首詩可以有不同的模板
	 * <注意>因為poem中的詞語在基因演算法中會被替換，所以每個PoemTemplate都要有一個poem的實體，
	 * 不可以單純複製reference，否則修改某個PoemTemplate的poem的某個詞的時候會影響到其他人
	 * 
	 * @param row
	 * @param col
	 * @param wordComposition
	 * @param poem 
	 */
	public PoemTemplate(int row,int col, PoemSentence[] poem){
		this.row = row;
		this.col = col;
		/*錯誤的複製 : this.poem = poem;*/
		this.poem = new PoemSentence[row];
		for ( int i = 0 ; i < row ; i++){
			this.poem[i] = new PoemSentence(poem[i].getSentenceType(), poem[i].getWords());
		}
		
		maxRhythmMatch = row/2;
		if (col == 5){
			maxToneMatch = 3*row;
		}
		else{
			maxToneMatch = 4*row;
		}
		maxAntithesisMatch= row*col/2;
		
		maxDiversityMatch = row*col;
		modified = true;
	}
	
	 public static PoemTemplate getCopy(PoemTemplate sourcePoem){
		return new PoemTemplate(sourcePoem.row, sourcePoem.col, sourcePoem.poem);
	}
	
	/**
     * 隨機產生一組模板並填入一首新的詩
     * @param row 詩有幾句
     * @param col 每句幾個字
     * @return PoemTemplate的object
	 * @throws MakeSentenceException 
     */
    public static PoemTemplate getRandomPoem(int row,int col,WordPile wordPile,MakeSentence maker){
    	
    	PoemSentence[] poem = new PoemSentence[row];
    	for (int i = 0 ; i < row ; i++){
    		try {
				poem[i]  = maker.makeRandomSentence();
			} catch (MakeSentenceException e) {
				System.err.println(e.getMessage());
				System.err.println("error : 無法隨機產生一首詩");
				System.exit(1);
			}
    	}

    	return new PoemTemplate(row, col, poem);
    }
    
	public PoemSentence[] getPoem() {
		modified = true;
		return poem;
	}
	
	public int getRow(){
		return this.row;
	}
	
	public int getCol(){
		return this.col;
	}
	
	/**
	 * 當呼叫 getPoem() 系統會認為使用者更改過詩的內容，因此要重新計算 "適應分數"
	 * 否則就直接回傳上次計算完的結果
	 * @return 適應分數
	 */
	public int getFitnessScore() {
		if (modified){
			modified = false;
			fitnessFunction();
		}
		return fitnessScore;
	}
	
	private void fitnessFunction(){
		detailScore[0] = getRhythmScore();
		detailScore[1] = getToneScore();
		detailScore[2] = getAntithesisScore();
		detailScore[3] = getDiversityScore();
		fitnessScore = 0;
		for (int score : detailScore)
			fitnessScore += score;
		if (DEBUG) System.out.println(this.printScore());
	}
	
	private int getDiversityScore(){
		int countUniqueChar = 0;
		HashMap<Character, Boolean> map = new HashMap<Character, Boolean>();
		char c;
		for (int i = 1 ; i <= row*col ; i++){
			c = getCharAt(i);
			if (!map.containsKey(c)){
				countUniqueChar += 1;
				map.put(c, true);
			}
		}
		
		if (DEBUG) System.out.printf("不重複的字共有 %d / %d 個\n",countUniqueChar,maxDiversityMatch);
		return MAX_DIVERSITY_SCORE*countUniqueChar/(row*col);
	}
	
	private int getAntithesisScore(){
		int countAntithesis = 0;
		//System.out.println(this);
		for ( int i = 0 ; i < row ; i += 2){
			//System.out.println("比較第 "+i+" 和 "+(i+1)+" 句");
			int index1 = 0, countLetter1 = 0;
			int index2 = 0, countLetter2 = 0;
			while (true){
				if (countLetter1 == col && countLetter2 == col)
					break;
				//System.out.printf("index1 = %d, #letter1 = %d\n",index1,countLetter1);
				//System.out.printf("index2 = %d, #letter2 = %d\n\n",index2,countLetter2);
				int len1 = poem[i].getWords()[index1].getLength();
				int len2 = poem[i+1].getWords()[index2].getLength();
				if ( (poem[i].getWords()[index1].getWordType() & poem[i+1].getWords()[index2].getWordType() ) > 0){
					if (DEBUG) System.out.printf("%s (%s) = %s (%s)\n",poem[i].getWords()[index1].getWord(),ChineseWord.getReadableWordType(poem[i].getWords()[index1].getWordType()),poem[i+1].getWords()[index2].getWord(),ChineseWord.getReadableWordType(poem[i+1].getWords()[index2].getWordType()));
					countAntithesis += Math.min(len1,len2);
					countLetter1 += len1;
					countLetter2 += len2;
					if(index1 +1 < poem[i].getLength())
						index1 += 1;
					if(index2 +1 < poem[i+1].getLength())
						index2 += 1;
				}
				else{
					if ( countLetter1 + len1 > countLetter2 + len2){
						countLetter2 += len2;
						index2 +=1;
					}
					else if (countLetter2 + len2> countLetter1 + len1){
						countLetter1 += len1;
						index1 += 1;
					}
					else{
						countLetter1 += len1;
						countLetter2 += len2;
						index1 += 1;
						index2 += 1;
					}
				}
			}
		}
		if (DEBUG) System.out.printf(">>對偶的詞共有  %d / %d 個\n",countAntithesis,maxAntithesisMatch);
		return countAntithesis*MAX_ANTITHESIS_SCORE/maxAntithesisMatch;
	}
	
	private int getToneScore(){
		
		int countMatchTone;
		int countMatchRhythmTone = 0;
		
		if (DEBUG) System.out.println("===平仄===");
		//分別用平起式 / 仄起式 模板去檢驗符合的字數，再取較高者
		countMatchTone = Math.max(getMatchToneCount(0), getMatchToneCount(2));
		
		/*處理韻腳的平仄*/
		if (DEBUG) System.out.println("===韻腳平仄===");
		if (poem[0].getWords()[poem[0].getLength()-1].getRythm() == poem[1].getWords()[poem[1].getLength()-1].getRythm()){
			if (getToneAt(1*col) == 0){
				countMatchRhythmTone += 1; /*首句押韻用平聲*/
				if (DEBUG) System.out.printf("第1句 : 平 (%c,%c)\n",getCharAt(col),getCharAt(2*col));
			}
		}
		else{
			if (getToneAt(1*col) == 1){
				countMatchRhythmTone += 1; /*首句不押韻用仄聲*/
				if (DEBUG) System.out.println("第1句 : 仄");
			}
		}
		if (getToneAt(2*col) == 0){
			countMatchRhythmTone += 1;
			if (DEBUG) System.out.println("第2句 : 平");
		}
		for (int i = 4 ; i <= row ; i += 2){
			if ( getToneAt((i-1)*col) == 1){
				countMatchRhythmTone += 1;
				if (DEBUG) System.out.println("第"+(i-1)+"句 : 仄");
			}
			if ( getToneAt(i*col) == 0){
				countMatchRhythmTone += 1;
				if (DEBUG) System.out.println("第"+i+"句 : 平");
			}
		}
		if (DEBUG)System.out.printf(">>符合平仄的字有 (%d + %d(韻腳相關)) / %d 個\n",countMatchTone,countMatchRhythmTone,maxToneMatch);
		return (countMatchTone+countMatchRhythmTone)*MAX_TONE_SCORE/maxToneMatch;
	}
	/**
	 * 套用 平起式 或 仄起式 的模板來計算符合平仄的字數
	 * @param type 0 : 平起式, 2 : 仄起式
	 * @return 符合平仄的字數
	 */
	private int getMatchToneCount(int type){
		final int[][] standardTone = new int[][]{{0,1,0},{1,0,1},{1,0,1},{0,1,0}};
		int countMatchTone = 0;
		
		if (type == 0){
			if (DEBUG) System.out.println("平起式");
		}
		else if (type == 2){
			if (DEBUG)System.out.println("仄起式");
		}
		else{
			System.err.println("error : invalid tone type");
		}
		
		for ( int i = 0 ; i < row ; i ++){
			for ( int j = 2, k = 0 ; j <= col ; j+=2, k++){
				int charIndex = i*col+j;
				if (DEBUG) System.out.printf("%c(%d)",getCharAt(charIndex),getToneAt(charIndex));
				if (getToneAt(charIndex) == standardTone[type][k]){
					countMatchTone += 1;
					if (DEBUG) System.out.print("O ");
				}
				else{
					if (DEBUG) System.out.print("X ");
				}
			}
			if (DEBUG)  System.out.println();
			type = (type+1)%4;
		}
		return countMatchTone;
	}
	
	/**
	 * 取得整首詩中的某個字
	 * @param index 從"1"開始算
	 * @return 
	 */
	private char getCharAt(int index){
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
		for ( ChineseWord word : poem[atRow].getWords()){
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
	private int getToneAt(int index){
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
		for ( ChineseWord word : poem[atRow].getWords()){
			int len = word.getLength();
			if (cumulativeSum + len >= index){
				return word.getToneAt(index-cumulativeSum-1);
			}
			cumulativeSum += len;
		}
		return -1;
	}
	
	private int getRhythmScore(){
		HashMap<Character,Integer> recordRhythm = new HashMap<Character,Integer>();
		int maxCountSameRhytm = 0;
		char mostRhythm='?';
		for (int  i = 1 ; i < row ; i += 2){
			char rhythm = poem[i].getWords()[poem[i].getLength()-1].getRythm();
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
		for (int i = 1 ; i < row ; i+= 2){
			if (DEBUG) System.out.print(" ("+poem[i].getWords()[poem[i].getLength()-1].getWord()+","+poem[i].getWords()[poem[i].getLength()-1].getRythm()+")");
		}
		if (DEBUG) System.out.println();
		if (DEBUG) System.out.printf(">>最多的韻腳是 \"%c\"，共有 %d / %d 個\n",mostRhythm,maxCountSameRhytm,maxRhythmMatch);
		return maxCountSameRhytm*MAX_RHYTHM_SCORE/maxRhythmMatch;
	}
	
	public String printScore(){
		this.getFitnessScore();
		return String.format("押韻: %d/%d, 平仄: %d/%d, 對偶:%d/%d, 多樣性:%d/%d",detailScore[0],MAX_RHYTHM_SCORE,detailScore[1],MAX_TONE_SCORE,detailScore[2],MAX_ANTITHESIS_SCORE,detailScore[3],MAX_ANTITHESIS_SCORE);
	}
	
	public int[] getDetailScore(){
		getFitnessScore();
		return detailScore;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (PoemSentence line : poem){
			sb.append(line.toString()+"\n");
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
