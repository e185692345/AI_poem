package ai;

import java.util.HashMap;

import ai.poem.PoemTemplate;
import ai.sentence.SentenceMaker;

public class TestMain {
	
	public static void main(String[] argv) {
		 int[] test = new int[10];
		 for(int i : test){
			 System.out.print(i+" ");
		 }
		 System.out.println();
		 addElement(test, 1, 1);
		 addElement(test, 3, 3);
		 addElement(test, 5, 5);
		 addElement(test, 7, 7);
		 addElement(test, 9, 9);
		 for(int i : test){
			 System.out.print(i+" ");
		 }
	}
	
	private static void addElement(int[] arr, int index, int value){
		arr[index] = value;
	}
}
