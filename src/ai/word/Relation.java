package ai.word;

import ai.exception.RelationConvertException;

public enum Relation {
	TOPIC(-3,"/r/topic","",""),
	PADDING(-2,"/r/padding","",""),
	IsA(0,"/r/IsA","名","名"),
	PartOf(1,"/r/PartOf","名","名"),
	HasProperty(2,"/r/HasProperty","名","形"),
	UsedFor(3,"/r/UsedFor","名","動"),
	CapableOf(4,"/r/CapableOf","名","動"),
	AtLocation(5,"/r/AtLocation","名","名"),
	Causes(6,"/r/Causes","名動","形動"),
	HasSubevent(7,"/r/HasSubevent","動","動"),
	HasFirstSubevent(8,"/r/HasFirstSubevent","動","動"),
	RelatedTo(9,"/r/RelatedTo","名形動","名形動"),
	HasPrerequisite(10,"/r/HasPrerequisite","動","動"),
	CreatedBy(11,"/r/CreatedBy","名","名"),
	MotivatedByGoal(12,"/r/MotivatedByGoal","動","名形動"),
	Desires(13,"/r/Desires","名","名動"),
	MadeOf(14,"/r/MadeOf","名","名"),
	HasA(15,"/r/HasA","名","名");
	
	public static final int START = 0;
	public static final int END = 1;
	public static final int TOTAL_RELATION = Relation.values().length; 
	
	private int index;
	private String str;
	int startWordType,endWordType;
	
	private Relation(int index, String str, String startWordType, String endWordType) {
		this.index = index;
		this.str = str;
		
		this.startWordType = convertWordType(startWordType);
		this.endWordType =  convertWordType(endWordType);
	}
	/**
	 * 把中文的詞性轉換成數字
	 * @param wordType 
	 * @return 
	 */
	private int convertWordType(String wordType){
		int value = 0;
		if (wordType.indexOf("名") != -1){
			value += ChineseWord.NOUN;
		}
		if (wordType.indexOf("形") != -1){
			value += ChineseWord.ADJ;	
		}
		if (wordType.indexOf("動") != -1){
			value += ChineseWord.VERB;
		}
		return value;
	}
	
	public int getIndex(){
		return this.index;
	}
	
	public String toString(){
		return this.str;
	}
	
	public static int getWordType(Relation relation,int startOrEnd){
		if (startOrEnd == START){
			return relation.startWordType;
		}
		else if (startOrEnd == END){
			return relation.endWordType;
		}
		else{
			System.err.println("error : the second parameter can either be 0 (START) or 1 (END)");
			System.exit(1);
			return -1;
		}
	}
	
	static public Relation getRelation(String str) throws RelationConvertException{
		for (Relation relation : Relation.values()){
			if ( relation.toString().equals(str))
				return relation;
		}
		throw new RelationConvertException(str);
	}
	
	static public Relation getRelation(int index) throws RelationConvertException{
		for (Relation relation : Relation.values()){
			if ( relation.getIndex() == index)
				return relation;
		}
		throw new RelationConvertException(index);
	}
}
