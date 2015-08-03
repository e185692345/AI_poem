package ai.exception;

import ai.sentence.MakeSentence;

public class MakeSentenceException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5622193619489341012L;
	String type;
	public MakeSentenceException(int sentenceType) {
		
		switch (sentenceType) {
		case MakeSentence.sentenceType0:
			type = "/r/HasProperty + 的 + /r/IsA";
			break;
		case MakeSentence.sentenceType1:
			type = "在 + /r/AtLocation + /r/CapableOf";
			break;
		case MakeSentence.sentenceType2:
			type = "TOPIC + 會/想 + /r/CapableOf";
			break;
		case MakeSentence.sentenceType3:
			type = "TOPIC + 是 + /r/IsA";
			break;
		case MakeSentence.sentenceType4:
			type = "可以 + /r/CapableOf";
			break;
		default:
			type="任何句型";
			break;
		}
	}
	
	public String getMessage(){
		return "無法產生 ( "+type+" ) 的句子";
	}
}
