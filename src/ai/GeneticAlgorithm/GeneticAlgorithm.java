package ai.GeneticAlgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;


import ai.exception.RelationWordException;
import ai.poem.PoemTemplate;
import ai.sentence.MakeSentence;
import ai.sentence.PoemSentence;
import ai.word.ChineseWord;
import ai.word.Relation;
import ai.word.WordPile;

public class GeneticAlgorithm {
	private static final boolean DEBUG = false ;
	//======================參數設定======================
    //族群大小
    private static final int POPULATION_SIZE = 100;
    //交配親代競爭群組大小
    private static final int SELECTED_GROUP_SIZE = 5;
    //突變機率
    private static final double MUTATION_RATE = 1;
    //終止條件
	private final int maxGeneration = 400;
	private final int targetScore = PoemTemplate.MAX_ANTITHESIS_SCORE + PoemTemplate.MAX_DIVERSITY_SCORE + PoemTemplate.MAX_TONE_SCORE + PoemTemplate.MAX_RHYTHM_SCORE;
	//===================================================
    
	/*詞庫*/
    private WordPile wordPile;
    private MakeSentence sentenceMaker;
    /*一個世代*/
    private PoemTemplate[] population;
	
    /*用於隨機產生不重複的數字*/
	private ArrayList<Integer> indexList = new ArrayList<Integer>();
	
	private int row, col;
	private final static Random rand = new Random();
	/**
	 * 
	 * @param row 詩有幾句
	 * @param col 每句幾個字
	 * @param wordPile 單詞庫
	 */
    public GeneticAlgorithm(int row, int col,WordPile wordPile, MakeSentence sentenceMaker) {
		this.wordPile = wordPile;
		this.sentenceMaker = sentenceMaker;
		this.row = row;
		this.col = col;
	}
    
    
    public void evole() {
		int[] maxScore = new int[maxGeneration];
		int[] minScore = new int[maxGeneration];
		int[] avgScore = new int[maxGeneration];
		int[][] detailScore = new int[PoemTemplate.COUNT_FITNESS_TYPE][maxGeneration];
    	int sumScore;
    	int[] sumDetailScore = new int[PoemTemplate.COUNT_FITNESS_TYPE];
    	int counPoint = 0;
    	
    	initPopulation();
    	if (DEBUG) printPoem();
    	if (!DEBUG) System.out.println("演化進度");
    	for ( int i = 0; i < maxGeneration ; i++){
    		if (DEBUG) System.out.println(" === 第"+i+"代 ===");
     		crossover();
			mutation();
			select();
			
			maxScore[i] = 0; minScore[i] = 1000000; sumScore = 0;
			for (int j = 0 ; j < PoemTemplate.COUNT_FITNESS_TYPE ; j++)
				sumDetailScore[j] = 0;
			
			for ( int j = 0 ; j < POPULATION_SIZE ; j++){
				int score = population[j].getFitnessScore();
				if (maxScore[i] < score)
					maxScore[i] = score;
				if (minScore[i] > score)
					minScore[i] = score;
				sumScore += score;
				for (int k = 0 ; k < PoemTemplate.COUNT_FITNESS_TYPE ; k++)
					sumDetailScore[k] += population[j].getDetailScore()[k];
			}
			avgScore[i] = sumScore/POPULATION_SIZE;
			for (int j = 0 ; j < PoemTemplate.COUNT_FITNESS_TYPE ; j++){
				detailScore[j][i] = sumDetailScore[j]/POPULATION_SIZE;
			}
			counPoint += 1;
			if (DEBUG) printPoem();
			
			if (!DEBUG)
				System.out.print(getProgressBar(i*100/maxGeneration));
			if (maxScore[i] >= targetScore)
				break;
    	}
    	if (!DEBUG) System.out.println();
    	
    	int percentage = 10;
    	int numberToPrint = (POPULATION_SIZE/percentage < 10) ? 10 : POPULATION_SIZE/percentage;
    	System.out.println("較好的詩");
    	for ( int i = 0 ; i < numberToPrint ; i++){
        	System.out.println("=== 第"+i+"首 ===");
        	System.out.println(population[i].printScore());
        	System.out.println(population[i].toString());
        }
    	
    	if (counPoint > 0){
    		new StatisticWindow(counPoint, maxScore, minScore, avgScore, detailScore);
    	}
	}
    
    private String getProgressBar(int percent){
    	StringBuilder sb = new StringBuilder();
    	sb.append("\r[");
    	for (int i = 0 ; i < 50 ; i++){
    		if (i==24){
    			sb.append(String.format("%2d%%", percent));
    		}
    		else if ( i != 25 && i != 26){
	    		if (i*2<=percent)
	    			sb.append('=');
	    		else
	    			sb.append(' ');
    		}
    	}
    	sb.append(']');
    	
    	return sb.toString();
    }
    /**
     * 初始化族群
     */
    public void initPopulation(){
    	population = new PoemTemplate[POPULATION_SIZE];
        for (int i = 0 ; i < POPULATION_SIZE ; i++){
           population[i] = PoemTemplate.getRandomPoem(this.row, this.col,this.wordPile,this.sentenceMaker);
        }
    }
    private void crossover(){
    	
    	final int crosoverTime = POPULATION_SIZE/SELECTED_GROUP_SIZE;
    	
    	if (DEBUG) System.out.println("===Crossover===");
    	
    	for (int i = 0 ; i < crosoverTime ; i++){
    		// ===== 第一部份 : 從 n 首詩取前2名 =====
    	    ParentSet parentSet = SelectParent();
    	    PoemTemplate poem1  = parentSet.poem1;
    	    PoemTemplate poem2  = parentSet.poem2;
    		// ===== 第二部份 : 用 16 句重組一首詩，注意相鄰兩句斷詞方式要相同 =====
    		PoemSentence[] newSentenceList = new PoemSentence[row];
    		HashMap<Integer, ArrayList<PoemSentence>> lineCompositionToSentences = new HashMap<Integer,ArrayList<PoemSentence>>();
    		ArrayList<PoemSentence> used = new ArrayList<PoemSentence>();
    		for (int j = 0 ; j < row ; j++){
    			int encode = poem1.getPoem()[j].getLineCompositionEncode();
    			if (!lineCompositionToSentences.containsKey(encode)){
    				lineCompositionToSentences.put(encode, new ArrayList<PoemSentence>());
    			}
				lineCompositionToSentences.get(encode).add(poem1.getPoem()[j]);
				used.add(poem1.getPoem()[j]);
    			
    			encode = poem2.getPoem()[j].getLineCompositionEncode();
    			if (!lineCompositionToSentences.containsKey(encode)){
    				lineCompositionToSentences.put(encode, new ArrayList<PoemSentence>());
    			}
    			lineCompositionToSentences.get(encode).add(poem2.getPoem()[j]);
				used.add(poem2.getPoem()[j]);
    		}
    		
    		for (int j = 0 ; j < row ; j += 2){
    			PoemSentence sentence1 = used.get(rand.nextInt(used.size()));
    			used.remove(sentence1);
    			ArrayList<PoemSentence> availableCoice = lineCompositionToSentences.get(sentence1.getLineCompositionEncode());
    			PoemSentence sentence2 = availableCoice.get(rand.nextInt(availableCoice.size()));
    			
    			used.remove(sentence2);
    			newSentenceList[j] = sentence1;
    			newSentenceList[j+1] = sentence2;
    		}
    		// ===== 第三部份 : 把新詩跟隨機一首舊詩比較，若分數較高就更新 =====
    		PoemTemplate newPoem = new PoemTemplate(row, col, newSentenceList);
    		int newIndex = rand.nextInt(POPULATION_SIZE);
    		PoemTemplate oldPoem = population[newIndex];
    		if (newPoem.getFitnessScore() > oldPoem.getFitnessScore()){
    			if (DEBUG) System.out.printf(" %d > %d ， 取代第 %d 首\n",newPoem.getFitnessScore(),oldPoem.getFitnessScore(),newIndex);
    			oldPoem = newPoem;
    		}
    	}
    	
    }
    
    private class ParentSet{
    	PoemTemplate poem1,poem2;
    	public ParentSet(PoemTemplate poem1,PoemTemplate poem2) {
			this.poem1 = poem1;
			this.poem2 = poem2;
		}
    }
    
    private ParentSet SelectParent(){
    	// ===== 第一部份 : 從 n 首詩取前2名 =====
    	PoemTemplate poem1 = population[getRandomIndex(POPULATION_SIZE)];
        PoemTemplate poem2 = population[getRandomIndex(0)];
        PoemTemplate tempPoem;
        
        if (poem2.getFitnessScore() > poem1.getFitnessScore()){
        	tempPoem = poem1;
        	poem1 = poem2;
        	poem2 = tempPoem;
        }
        for ( int j = 0 ; j < SELECTED_GROUP_SIZE - 2 ; j++){
    		tempPoem = population[getRandomIndex(0)];
    		if ( tempPoem.getFitnessScore() >= poem1.getFitnessScore()){
    			poem2 = poem1;
    			poem1 = tempPoem;
    		}
    		else if ( tempPoem.getFitnessScore() > poem2.getFitnessScore()){
    			poem2 = tempPoem;
    		}
    	}
        return new ParentSet(poem1, poem2);
    }
    
	/**
	 * 	逐一替換每個詞，若替換後分數較低則把詩復原
	 */
    private void mutation(){
		if (DEBUG) System.out.println("===Mutation===");
		
		for(int i = 0 ; i < POPULATION_SIZE ; i ++){
			if (!canHappen(MUTATION_RATE))
				continue;
			for (int j = 0 ; j < row ; j++){
				for (int k = 0; k < population[i].getPoem()[j].getLength() ; k++){
					int oldScore = population[i].getFitnessScore();
					ChineseWord oldWord = population[i].getPoem()[j].getWords()[k];
					population[i].getPoem()[j].getWords()[k] = mutateAWord(i,j,k);
					if ( population[i].getFitnessScore() < oldScore){
						population[i].getPoem()[j].getWords()[k] = oldWord;
					}
					else if ( population[i].getFitnessScore() > oldScore){
						if (DEBUG) System.out.printf("第 %d 首詩 (%d,%d) %s => %s, 分數 %d => %d\n",i,j,k,oldWord.getWord(),population[i].getPoem()[j].getWords()[k].getWord(),oldScore,population[i].getFitnessScore());
					}
				}
			}
		}
	}
    
    private ChineseWord mutateAWord(int poemIndex, int chosenRow, int chosenCol){
    	ChineseWord word = population[poemIndex].getPoem()[chosenRow].getWords()[chosenCol];
    	Relation relation = word.getRelation();
		int startOrEnd = word.getStartOrEnd();
		int length = word.getLength();
		/*比0小的是 padding 跟 topic*/
		if (relation.getIndex() >= 0){
			try {
				return wordPile.getRlationWord(relation, startOrEnd, length);
			} catch (RelationWordException e) {
				System.err.println(e.getMessage());
				return word;
			}
		}
		else if (relation == Relation.PADDING){
			try {
				return sentenceMaker.getAPaddingWord(population[poemIndex].getPoem()[chosenRow].getSentenceType(),chosenCol);
			} catch (Exception e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
				return word;
			}
		}
		else {
			return word;
		}
		
    }
	/**
	 * 用轉盤法篩選出下一個世代，最後10%一律捨棄重新隨機產生
	 */
    private void select(){
		int[] cumulativeSum = new int[POPULATION_SIZE];
		
		int totalSum = 0;
		final int head = 0;
		final int tail = POPULATION_SIZE/4;
		final int middle = POPULATION_SIZE - head - tail;
		
		Arrays.sort(population);
		
		PoemTemplate[] populationCopy = new PoemTemplate[POPULATION_SIZE];
		for (int i = 0 ; i < POPULATION_SIZE ; i++){
			populationCopy[i] = population[i];	 
		}
		
		if ( middle > 0){
			for ( int i = 0 ; i < middle ; i++){
				if (i ==0)
					cumulativeSum[i] = population[i+head].getFitnessScore();
				else
					cumulativeSum[i] = cumulativeSum[i-1] + population[i+head].getFitnessScore();
			}
			
			totalSum = cumulativeSum[middle-1];
			
			if (DEBUG)  System.out.println("===Select===");
			
			for (int i = 0 ; i < middle ; i++){
				int nextIndex = rand.nextInt(totalSum)+1;
				for ( int j = 0 ;j< middle ; j++){
					if ( cumulativeSum[j] >= nextIndex){
						if (DEBUG)  System.out.println("第  "+(head+i)+" 次；選到第 "+(head+j)+" 首");
						population[head+i] = PoemTemplate.getCopy(populationCopy[head+j]);
						break;
					}
				}
			}
		}
		
		for ( int i = 0 ; i < tail ; i++){
			population[POPULATION_SIZE-1-i] = PoemTemplate.getRandomPoem(row, col,wordPile,sentenceMaker);
		}
		
	}
	
    /**
     * 	回傳 0 - (bound-1) 之間的隨機整數
     *	如果 bound <= 0，則會傳上次0 - bound中還沒被選到的隨機整數
     * @param bound 現住回傳的隨機數字的範圍
     * @return 0 ~ (bound-1) 之間的隨機整數
     */
	private int getRandomIndex(int bound){
		int temp,index;
		
		if ( bound > 0){
			indexList.clear();
			for ( int i = 0 ; i < bound ; i++)
				indexList.add(i);
		}
		if ( indexList.isEmpty())
			return 0;
		index = rand.nextInt(indexList.size());
		temp = indexList.get(index);
		indexList.remove(index);
		return temp;
	}	


    /**
     * 決定某機率(在0~1之間)是否發生
     * @param probability 0-1之間的小數
     * @return ture(發生)/flase(不發生)
     */
    private boolean canHappen(double probability){
        if (probability >= 1)
            return true;
        else{
            if ((int)(rand.nextDouble()*100) < (int)(probability*100) )
                return true;
            else{
				return false;
			}
        }
    }
    
    /**
     * 印出所有的詩
     */
    private void printPoem(){
        for (int i = 0 ; i < POPULATION_SIZE ; i++){
            System.out.println("第 "+i+" 首詩 ");
            System.out.println(population[i].printScore());
            System.out.println(population[i].toString());
        }
    }
}
