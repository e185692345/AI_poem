package ai.GeneticAlgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import ai.poem.LineComposition;
import ai.poem.PoemTemplate;
import ai.word.ChineseWord;
import ai.word.WordPile;

public class GeneticAlgorithm {
	private static final boolean DEBUG = false ;
	 private final static Random rand = new Random();
	 
    /*族群大小*/
    private static final int populationSize = 100;
    /*交配機率*/
    private static final double crossoverRate = 0.5;
    /*突變機率*/
    private static final double mutationRate = 0.1;
    
    /*詞庫*/
    private WordPile wordPile;
  
    /*一個世代*/
    private PoemTemplate[] population;
	
    /*用於隨機產生不重複的數字*/
	private ArrayList<Integer> indexList = new ArrayList<Integer>();
	
	/*終止條件*/
	private final int maxGeneration = 400;
	private final int targetScore = PoemTemplate.scoreAntithesis + PoemTemplate.scoreDiversity + PoemTemplate.scoreTone + PoemTemplate.scoreRhyme;
	
	private int row, col;
	
	/**
	 * 
	 * @param row 詩有幾句
	 * @param col 每句幾個字
	 * @param wordPile 單詞庫
	 */
    public GeneticAlgorithm(int row, int col,WordPile wordPile) {
		this.wordPile = wordPile;
		this.row = row;
		this.col = col;
	}
    
    
    public void Evole() {
		int[] maxScore = new int[maxGeneration];
		int[] minScore = new int[maxGeneration];
		int[] avgScore = new int[maxGeneration];
    	int sumScore;
    	int counPoint = 0;
    	
    	InitPopulation();
    	if (DEBUG) PrintPoem();
    	for ( int i = 0 ; i < maxGeneration ; i++, counPoint ++){
    		Crossover();
			Mutation();
			Select();
			maxScore[i] = 0; minScore[i] = 1000000; sumScore = 0;
			for ( int j = 0 ; j < populationSize ; j++){
				int score = population[j].getFitnessScore();
				if (maxScore[i] < score)
					maxScore[i] = score;
				if (minScore[i] > score)
					minScore[i] = score;
				sumScore += score;
			}
			avgScore[i] = sumScore/populationSize;
			if (DEBUG) PrintPoem();
			if (maxScore[i] >= targetScore)
				break;
    	}
    	PrintPoem();
    	if (counPoint > 0)
    		new StatisticWindow(new GenerationData(counPoint, maxScore, minScore, avgScore));
	}
    /**
     * 初始化族群
     */
    public void InitPopulation(){
    	population = new PoemTemplate[populationSize];
        for (int i = 0 ; i < populationSize ; i++){
            population[i] = RandomPoem(this.row, this.col);
        }
        if (DEBUG) PrintPoem();
    }
    
	/**
	 * 隨機取2首詩並交換其中的2個詞
	 */
    private void Crossover(){

		final int count_crossover = 2;
        int id1,id2;
        
		if (DEBUG) System.out.println("===Crossover===");
        for (int i = 0; i < populationSize/2 ; i++){
            if ( CanHappen(crossoverRate)){
            	id1 = RandIndex(populationSize);
                id2 = RandIndex(0);
				for (int j = 0 ; j < count_crossover ; j++){
					if (DEBUG) System.out.printf("第%d首 <==> 第%d首\n",id1,id2);
					ExchangeTwoWords(population[id1],population[id2]);
				}
				
				if (DEBUG){
					System.out.println("第 "+id1+" 首詩 = \n"+ population[id1].toString());
					System.out.println("第 "+id2+" 首詩 = \n"+ population[id2].toString());
				}
            }
			if (DEBUG) System.out.println();
        }
    }
    
	/**
	 * 	將一個隨機位置的詞替換掉
	 */
    private void Mutation(){
		int row,col;
		if (DEBUG) System.out.println("===Mutation===");
		for(int i = 0 ; i < populationSize ; i ++){
			if (CanHappen(mutationRate)){
				ChineseWord[][] poem = population[i].getPoem();
				row = rand.nextInt(poem.length);
				col = rand.nextInt(poem[row].length);
				if (DEBUG) System.out.printf("Mutation(%d) at (%d,%d)\n",i,row,col);
				poem[row][col] = wordPile.GetAWord(ChineseWord.all,population[i].getTeplate()[row][col]);
				if (DEBUG) System.out.println("第 "+i+" 首詩 =\n "+ population[i].toString());
			}
		}
	}
	/**
	 * 用轉盤法篩選出下一個世代
	 */
    private void Select(){
		
		int[] cumulativeSum = new int[populationSize];
		
		int totalSum = 0;
		int head = populationSize/10,tail = head, middle = populationSize - head - tail;
		
		Arrays.sort(population);
		
		PoemTemplate[] populationCopy = new PoemTemplate[populationSize];
		for (int i = 0 ; i < populationSize ; i++){
			populationCopy[i] = population[i];	 
		}
		
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
					population[head+i] = new PoemTemplate(row, col, populationCopy[j].getTeplate(), populationCopy[j].getPoem());
					break;
				}
			}
		}
		
		for ( int i = 0 ; i < tail ; i++){
			population[populationSize-1-i] = RandomPoem(row, col);
		}
		
	}
	/**
	 * 隨機選擇第一首詩中隨機一個詞，如果在第二首詩中也有找到一個長度相同的詞就將兩個詞交換
	 * @param poemTemplate1  第一首詩
	 * @param poemTemplate2  第二首詩
	 */
    private void ExchangeTwoWords(PoemTemplate poemTemplate1, PoemTemplate poemTemplate2){
    	int[][] template1 = poemTemplate1.getTeplate();
    	int row1 = rand.nextInt(template1.length);
    	int col1 = rand.nextInt(template1[row1].length);
    	
    	int[][] template2 = poemTemplate2.getTeplate();
    	int startRow = rand.nextInt(template2.length);
    	int startCol = rand.nextInt(template2[startRow].length);
    	int row2 = startRow, col2 = startCol;
    	ChineseWord temp;
    	
    	while(true){
    		if (template2[row2][col2] == template1[row1][col1]){
    			ChineseWord[][] poem1 = poemTemplate1.getPoem();
    			ChineseWord[][] poem2 = poemTemplate2.getPoem();
    			if (DEBUG) System.out.printf("(%d,%d)\"%s\" <=> (%d,%d)\"%s\"\n",row1,col1,poem1[row1][col1].getWord(),row2,col2,poem2[row2][col2].getWord());
    			temp = poem1[row1][col1];
    			poem1[row1][col1] = poem2[row2][col2];
    			poem2[row2][col2] = temp;
    			break;
    		}
    		col2 += 1;
    		if ( col2 == template2[row2].length){
    			col2 = 0;
    			row2 += 1;
    		}
    		if (row2 == template2.length){
    			row2 = 0;
    		}
    		if (row2 == startRow && col2 == startCol)
    			break;
    	}
    }
	
    /**
     * 	回傳 0 - (bound-1) 之間的隨機整數
     *	如果 bound <= 0，則會傳上次0 - bound中還沒被選到的隨機整數
     * @param bound 現住回傳的隨機數字的範圍
     * @return 0 ~ (bound-1) 之間的隨機整數
     */
	private int RandIndex(int bound){
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
    private boolean CanHappen(double probability){
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
    private void PrintPoem(){
        for (int i = 0 ; i < populationSize ; i++){
            System.out.println("第 "+i+" 首詩 \n"+population[i].toString()+"\n");
        }
    }
    
    /**
     * 隨機產生一組模板並填入一首新的詩
     * @param row 詩有幾句
     * @param col 每句幾個字
     * @return PoemTemplate的object
     */
    private PoemTemplate RandomPoem(int row,int col){
    	
    	int[][] wordComposition = new int[row][];
    	ChineseWord[][] poem = new ChineseWord[row][];
		for ( int i = 0 ; i < row ; i += 2){
			wordComposition[i]  = LineComposition.GetRandomComposition(col).clone();
			wordComposition[i+1]  = wordComposition[i];
			poem[i] = new ChineseWord[wordComposition[i].length];
			poem[i+1] = new ChineseWord[wordComposition[i+1].length];
			if (wordComposition[i].length != poem[i].length || wordComposition[i+1].length != poem[i+1].length){
				System.err.println("error : template's size and poem's doesn't match");
				System.exit(1);
			}
		}
		 
    	for (int i = 0 ; i < wordComposition.length ; i++){
    		for ( int j = 0 ; j < wordComposition[i].length ; j++){
    			poem[i][j] = wordPile.GetAWord(ChineseWord.noun|ChineseWord.adj|ChineseWord.verb, wordComposition[i][j]); 
    		}
    	}
    	return new PoemTemplate(row, col, wordComposition, poem);
    }
}
