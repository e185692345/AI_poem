package ai.exception;

public class MakeSentenceException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5622193619489341012L;
	int[] lineComposition;
	public MakeSentenceException(int[] lineComposition) {
		this.lineComposition =lineComposition;
	}
	
	public String getMessage(){
		StringBuilder sb = new StringBuilder();
		sb.append("無法用 [");
		for ( int element : lineComposition){
			sb.append(" "+String.valueOf(element));
		}
		sb.append(" ] 來造句");
		return sb.toString();
	}
}
