package ai.word;

public class Relation {
	
	public static final int START = 0;
	public static final int END = 1;
	/**
	 * 務必確保 TOTAL_RELATION 的值是最大的，TOTAL_RELATION 決定了 WordPile 中要 new 幾個 ArrayList
	 */
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
	public static final int TOTAL_RELATION = 15;
	
	/**
	 * 將relarion轉換成對應的數字，被排除的relation會回傳-1
	 * // TODO 新增Exception
	 * @param relation
	 * @return 
	 */
	public static int GetRelationID(String relation){
		int id;
		
		switch (relation) {
		case "/r/IsA":
			id = IsA;
			break;
		case "/r/PartOf":
			id = PartOf ;
			break;
		case "/r/UsedFor":
			id = UsedFor ;
			break;
		case "/r/CapableOf":
			id = CapableOf ;
			break;
		case "/r/AtLocation":
			id = AtLocation ;
			break;
		case "/r/Causes":
			id = Causes ;
			break;
		case "/r/HasSubevent":
			id = HasSubevent ;
			break;
		case "/r/HasFirstSubevent":
			id = HasFirstSubevent ;
			break;
		case "/r/RelatedTo":
			id = RelatedTo ;
			break;
		case "/r/HasPrerequisite":
			id = HasPrerequisite ;
			break;
		case "/r/HasProperty":
			id = HasProperty ;
			break;
		case "/r/MotivatedByGoal":
			id = MotivatedByGoal ;
			break;
		case "/r/Desires":
			id = Desires;
			break;
		case "/r/CreatedBy":
			id = CreatedBy;
			break;
		case "/r/MadeOf":
			id = MadeOf;
			break;
		default:
			id = -1;
			break;
		}
		
		return id;
	}
}
