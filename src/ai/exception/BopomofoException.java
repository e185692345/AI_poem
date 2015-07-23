package ai.exception;

public class BopomofoException extends Exception{

	/**
	 *	處理從教育部國語辭典讀取注音時遇到例外情況
	 *	1. BopomofoException(String word) : 
	 *		找不到某個詞的注音
	 *	2. BopomofoException (String word,int wordLength, int bopomofoLength)
	 *		注音跟詞的長度不符合，通常是有標點符號或是是有"ㄦ"出現時	
	 */
	private static final long serialVersionUID = 1L;
	
	public BopomofoException(String word){
		System.err.println("warning : Can't find word("+word+") and its bopomofo");
	}
	
	public BopomofoException (String word,int wordLength, int bopomofoLength){
		System.err.println("warning : Bopomofo's length("+bopomofoLength+") is diffferent from word's("+word+") length("+wordLength+")");
	}
}
