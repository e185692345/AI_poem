package ai.GeneticAlgorithm;

import java.util.ArrayList;
import java.util.Arrays;
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
	 private final static Random rand = new Random();
	 
    /*族群大小*/
    private static final int populationSize = 100;
    /*交配機率*/
    private static final double crossoverRate = 0.5;
    /*突變機率*/
    private static final double mutationRate = 0.1;
    
    /*詞庫*/
    private WordPile wordPile;
    private MakeSentence sentenceMaker;
    /*一個世代*/
    private PoemTemplate[] population;
	
    /*用於隨機產生不重複的數字*/
	private ArrayList<Integer> indexList = new ArrayList<Integer>();
	
	/*終止條件*/
	private final int maxGeneration = 400;
	private final int targetScore = PoemTemplate.maxAntithesisScore + PoemTemplate.maxDiversityScore + PoemTemplate.maxToneScore + PoemTemplate.maxRhymeScore;
	
	private int row, col;

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
    
    
    public void Evole() {
		int[] maxScore = new int[maxGeneration];
		int[] minScore = new int[maxGeneration];
		int[] avgScore = new int[maxGeneration];
    	int sumScore;
    	int counPoint = 0;
    	
    	InitPopulation();
    	if (DEBUG) PrintPoem();
    	for ( int i = 0 ; i < maxGeneration ; i++, counPoint ++){
    		if (DEBUG) System.out.println(" === 第"+i+"代 ===");
    		//Crossover();
    		SelectedCrossover();
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
           population[i] = PoemTemplate.RandomPoem(this.row, this.col,this.wordPile,this.sentenceMaker);
        }
    }
    
	/**
	 * 隨機取2首詩並交換其中的1個句子
	 */
    private void Crossover(){
          
		if (DEBUG) System.out.println("===Crossover===");
        for (int i = 0; i < populationSize/2 ; i++){
            if ( CanHappen(crossoverRate)){
            	int id1 = RandIndex(populationSize);
            	// TODO
            	/*if (id1 == 0)
            		id1 = RandIndex(0);*/
                int id2 = RandIndex(0);
                // TODO
                /*if (id2 == 0)
            		id2 = RandIndex(0);*/
				int index1 = RandIndex(this.row);
				int index2 = RandIndex(0);
				if (DEBUG) System.out.printf("第%d首  的第 %d <==> 第%d首 的第 %d 句\n",id1,index1,id2,index2);
				PoemSentence tmpSentence = population[id1].getPoem()[index1];
				population[id1].getPoem()[index1] = population[id2].getPoem()[index2];
				population[id2].getPoem()[index2] = tmpSentence;
				
				
				if (DEBUG){
					System.out.println("第 "+id1+" 首詩 = \n"+ population[id1].toString());
					System.out.println("第 "+id2+" 首詩 = \n"+ population[id2].toString());
				}
            }
			if (DEBUG) System.out.println();
        }
    }
    
    private void SelectedCrossover(){
    	boolean isFirst = true;
    	if (DEBUG) System.out.println("===Crossover===");
    	for (int i = 0 ; i < populationSize/2 ; i++){
    		if ( CanHappen(crossoverRate)){
    			PoemTemplate poem1;
    	    	PoemTemplate poem2;
    			if (isFirst){
    				poem1 = population[RandIndex(populationSize)];
    				isFirst = false;
    			}
    			else
    				poem1= population[RandIndex(0)];
    			poem2 = population[RandIndex(0)];
    			PoemSentence[] newSentenceList = new PoemSentence[row];
    			newSentenceList[0] = chooseSentence(RandIndex(row*2), poem1, poem2);
    			for ( int j = 1 ; j < row; j++){
    				newSentenceList[j] = chooseSentence(RandIndex(0), poem1, poem2);
    			}
    			PoemTemplate newPoem = new PoemTemplate(row, col, newSentenceList);
    			int newIndex = rand.nextInt(populationSize);
    			PoemTemplate oldPoem = population[newIndex];
    			if (newPoem.getFitnessScore() > oldPoem.getFitnessScore()){
    				if (DEBUG) System.out.printf(" %d > %d ， 取代第 %d 首\n",newPoem.getFitnessScore(),oldPoem.getFitnessScore(),newIndex);
    				oldPoem = newPoem;
    			}
    		}
    	}
    	
    }
    
    private PoemSentence chooseSentence(int index,PoemTemplate poem1, PoemTemplate poem2){
    	if (index < row){
    		return poem1.getPoem()[index];
    	}
    	else{
    		return poem2.getPoem()[index-row];
    	}
    }
	/**
	 * 	將一個隨機位置的詞替換掉
	 */
    private void Mutation(){
		int chosenRow,chosenCol;
		if (DEBUG) System.out.println("===Mutation===");
		
		for(int i = 0 ; i < populationSize ; i ++){
			// TODO
			//if ( i == 0) continue;
			if (CanHappen(mutationRate)){
				
				chosenRow = rand.nextInt(this.row);
				chosenCol = rand.nextInt(population[i].getPoem()[chosenRow].length());
				
				ChineseWord word = population[i].getPoem()[chosenRow].getWords()[chosenCol];
				int relation = word.getRelation();
				int startOrEnd = word.getStartOrEnd();
				int length = word.getLength();
				if (relation >= 0){
					try {
						population[i].getPoem()[chosenRow].getWords()[chosenCol] = wordPile.GetRlationWord(relation, startOrEnd, length);
						if (DEBUG) System.out.printf("Mutation(%d) at (%d,%d) : %s <=> %s\n",i,chosenRow,chosenCol,word.getWord(),population[i].getPoem()[chosenRow].getWords()[chosenCol].getWord());
						if (DEBUG) System.out.println("第 "+i+" 首詩 =\n "+ population[i].toString());
					} catch (RelationWordException e) {
						System.err.println(e.getMessage());
						System.err.printf("第 %d 首詩的(%d,%d)位置突變失敗\n",i,chosenRow,chosenCol);
					}
				}
				else if (relation == Relation.PADDING){
					word = wordPile.getAPaddingWord(population[i].getPoem()[chosenRow].getSentenceType());
				}
			}
		}
	}
	/**
	 * 用轉盤法篩選出下一個世代
	 */
    private void Select(){
		int[] cumulativeSum = new int[populationSize];
		
		int totalSum = 0;
		int head = 0,tail = populationSize/10, middle = populationSize - head - tail;
		
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
					population[head+i] = PoemTemplate.getCopy(populationCopy[head+j]);
					break;
				}
			}
		}
		
		for ( int i = 0 ; i < tail ; i++){
			population[populationSize-1-i] = PoemTemplate.RandomPoem(row, col,wordPile,sentenceMaker);
		}
		
	}
	/**
	 * 隨機選擇第一首詩中隨機一個詞，如果在第二首詩中也有找到一個長度相同的詞就將兩個詞交換
	 * @param poemTemplate1  第一首詩
	 * @param poemTemplate2  第二首詩
	 */
    /*private void ExchangeTwoWords(PoemTemplate poemTemplate1, PoemTemplate poemTemplate2){
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
    }*/
	
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
            System.out.println("第 "+i+" 首詩 ");
            System.out.println(population[i].printScore());
            System.out.println(population[i].toString());
        }
    }
}
