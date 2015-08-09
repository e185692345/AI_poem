package ai.word;

import ai.exception.RelationConvertException;

public enum Relation {
	TOPIC(-3,"/r/topic"),
	PADDING(-2,"/r/padding"),
	IsA(0,"/r/IsA"),
	PartOf(1,"/r/PartOf"),
	HasProperty(2,"/r/HasProperty"),
	UsedFor(3,"/r/UsedFor"),
	CapableOf(4,"/r/CapableOf"),
	AtLocation(5,"/r/AtLocation"),
	Causes(6,"/r/Causes"),
	HasSubevent(7,"/r/HasSubevent"),
	HasFirstSubevent(8,"/r/HasFirstSubevent"),
	RelatedTo(9,"/r/RelatedTo"),
	HasPrerequisite(10,"/r/HasPrerequisite"),
	CreatedBy(11,"/r/CreatedBy"),
	MotivatedByGoal(12,"/r/MotivatedByGoal"),
	Desires(13,"/r/Desires"),
	MadeOf(14,"/r/MadeOf"),
	HasA(15,"/r/HasA");
	
	public static final int START = 0;
	public static final int END = 1;
	public static final int TOTAL_RELATION = Relation.values().length; 
	
	private int index;
	private String str;
	
	private Relation(int index, String str) {
		this.index = index;
		this.str = str;
	}
	
	public int getIndex(){
		return this.index;
	}
	
	public String toString(){
		return this.str;
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
