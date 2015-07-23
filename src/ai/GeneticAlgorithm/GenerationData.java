package ai.GeneticAlgorithm;

public class GenerationData {
	
	int[] max, min, average;
	int maxValue,minValue;
	
	public GenerationData(int countPoint,int[] max, int[] min, int[] average){

		this.max = new int[countPoint];
		this.min = new int[countPoint];
		this.average = new int[countPoint];
		maxValue = 0;
		minValue = 1000000;
		for ( int i = 0 ; i < countPoint ; i++){
			if ( maxValue < max[i])
				maxValue = max[i];
			if (minValue > min[i])
				minValue = min[i];
		}
		
		for (int i = 0 ; i < countPoint ; i++){
			this.max[i] = max[i];
			this.min[i] = min[i];
			this.average[i] = average[i];
		}
	}
	
	public int[] getMax(){
		return this.max;
	}
	
	public int[] getMin(){
		return this.min;
	}
	
	public int[] getAverage(){
		return this.average;
	}
	
	public int getMaxValue(){
		return this.maxValue;
	}
	
	public int getMinValue(){
		return this.minValue;
	}
}
