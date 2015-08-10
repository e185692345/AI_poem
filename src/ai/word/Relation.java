package ai.word;

public class Relation {
	
	public static final int START = 0;
	public static final int END = 1;
	
	public static final int TOPIC = -3;
	public static final int PADDING = -2;
	public static final int ELSE = -1;
	public static final int IsA = 0;
	public static final int PartOf = 1;
	public static final int HasProperty = 2;
	public static final int UsedFor = 3;
	public static final int CapableOf = 4;
	public static final int AtLocation = 5;
	public static final int Causes = 6;
	public static final int HasSubevent = 7;
	public static final int HasFirstSubevent = 8;
	public static final int RelatedTo = 9;
	public static final int HasPrerequisite = 10;
	public static final int CreatedBy  = 11;
	public static final int MotivatedByGoal = 12;
	public static final int Desires = 13;
	public static final int MadeOf = 14;
	
	
	private final static String[] relation = {"/r/IsA","/r/PartOf","/r/HasProperty","/r/UsedFor","/r/CapableOf",
								 "/r/AtLocation","/r/Cause","/s/HasSubevent","/r/HasFirstSubevent",
								 "/r/RelatedTo","/r/HasPrerequisite","/r/CreatedBy","/r/MotivatedByGoal",
								 "/r/Desires","/r/MadeOf"};
	public static final int TOTAL_RELATION = relation.length;
	
	/**
	 * 將relarion轉換成對應的數字，被排除的relation會回傳-1
	 * // TODO 新增Exception
	 * @param relation
	 * @return 
	 */
	public static int getRelationID(String relation){
		for ( int i = 0 ; i < TOTAL_RELATION ;i++){
			if (relation.equals(Relation.relation[i]))
				return i;
		}
		
		return -1;
	}
	
	public static String getRelation(int id){
		return relation[id];
	}
	
}
