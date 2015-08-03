package ai.exception;

import ai.word.Relation;

public class RelationWordException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4154851293944225959L;
	private String message;
	public RelationWordException(int relation, int length) {
		message = "找不到 rel = "+Relation.getRelation(relation)+", 長度 = "+length+" 的詞\n";
	}
	
	public String getMessage(){
		return message;
	}
	
	
}
