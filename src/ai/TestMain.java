package ai;

import ai.sentence.SentenceMaker;

public class TestMain {
	
	public static void main(String[] argv) {
		System.out.println(SentenceMaker.isMultipleRelationValid("教室", "上課"));
		System.out.println(SentenceMaker.isMultipleRelationValid("籠子","吃飯"));
	}
}
