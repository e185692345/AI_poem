package ai.sentence;

import java.util.Random;

public class LineComposition {
	
	public final static int[][] FIVE_LETTER_COMPOSITION 
	= new int[][]{{2,3}/*,{3,2}*/,{1,1,1,1,1},
		{1,1,3}/*,{1,3,1},{3,1,1}*/,
		{2,2,1},{2,1,2},/*{1,2,2},*/
		{1,1,1,2},{1,1,2,1}/*,{1,2,1,1}*/,{2,1,1,1}};

	private static final Random rand = new Random();
	public static int[] GetRandomComposition(int countLetter){
		if (countLetter == 5){
			return FIVE_LETTER_COMPOSITION[rand.nextInt(FIVE_LETTER_COMPOSITION.length)];
		}
		
		return null;
	}
}
