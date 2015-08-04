package ai.word;

public class ChineseWord {

	public static final int padding = 8, noun = 1, adj = 2, verb = 4, all=7;
	
	private int length;
	private String word;
	private char[] bopomofo;
	private int[] tone;
	private int wordType;
	private int relation;
	private int startOrEnd;
	/**
	 * 	建立一個詞，儲存詞性和每個字的平仄、韻腳
	 * 	@param word: 中文字詞
	 * 	@param letterBopomofo: 每個字的注音
	 * 	@param wordType: 代表詞性的數字
	 * 
	 * 	名詞: 1, 形容詞: 2, 動詞: 4
	 * 	因為詞性是從concept net 的 relation 推測的，所以一個詞可能會有多種詞性
	 * 	用二進位表示詞性需要3bit(van) 某個bit是1就表示具有該詞性 
	 * 	例如: 詞性 = 5,van = 101, 表示是動詞和名詞
	 */
	public ChineseWord(String word, String[] letterBopomofo, int wordType, int relation, int startOrEnd){
		String str;
		
		this.length = word.length();
		this.bopomofo = new char[this.length];
		this.tone = new int[this.length];
		this.word = word;
		this.wordType = wordType;
		this.relation = relation;
		this.startOrEnd = startOrEnd;
		
		for (int i = 0 ; i < this.length ; i++){
			str = letterBopomofo[i];
			
			if (str.charAt(str.length()-1) =='˙'){
				this.tone[i] = 0;
				this.bopomofo[i] = str.charAt(str.length()-2);
			}
			else if (str.charAt(str.length()-1) == 'ˊ'){
				this.tone[i] = 0;
				this.bopomofo[i] = str.charAt(str.length()-2);
			}
			else if (str.charAt(str.length()-1) == 'ˇ'){
				this.tone[i] = 1;
				this.bopomofo[i] = str.charAt(str.length()-2);
			}
			else if (str.charAt(str.length()-1) == 'ˋ'){
				this.tone[i] = 1;
				this.bopomofo[i] = str.charAt(str.length()-2);
			}
			else{
				this.tone[i] = 0;
				this.bopomofo[i] = str.charAt(str.length()-1);
			}
		}
	}
	
	public ChineseWord(String word, char[] bopomofo, int[] tone, int wordType, int length, int relation, int startOrEnd) {
		this.bopomofo = bopomofo;
		this.length = length;
		this.tone = tone;
		this.word = word;
		this.wordType = wordType;
		this.relation = relation;
		this.startOrEnd = startOrEnd;
	}
	
	public int getStartOrEnd(){
		return this.startOrEnd;
	}
	
	public int getRelation(){
		return this.relation;
	}
	
	public int getLength() {
		return length;
	}

	public String getWord() {
		return word;
	}
	public char[] getBopomofo() {
		return bopomofo;
	}
	public void setBopomofo(char[] bopomofo) {
		this.bopomofo = bopomofo;
	}
	public int[] getTone() {
		return tone;
	}
	public void setTone(int[] tone) {
		this.tone = tone;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public void setWordType(int wordType) {
		this.wordType = wordType;
	}
	
	public char getCharAt(int index) {
		if (index >= length){
			System.err.println("error : index out of bound");
			System.exit(1);
			return '?';
		}
		else{
			return word.charAt(index);
		}
	}
	
	/*取得平仄*/
	public int getToneAt(int index) {
		if (index >= length){
			System.err.println("error : index out of bound");
			System.exit(1);
			return -1;
		}
		else{
			return tone[index];
		}
	}
	
	/*取得韻腳*/
	public char GetRythm(){
		return bopomofo[length-1];
	}
	/*取得詞性*/
	public int getWordType() {
		return wordType;
	}
	public static String ReadableWordType(int wordType){
		String type = new String();
		if (wordType == 0)
			return "填充詞";
		if ((wordType & noun )> 0)
			type += "名";
		if ((wordType & adj )> 0)
			type += "形";
		if ((wordType & verb )> 0)
			type += "動";
		if ((wordType & padding )> 0)
			type += "填";
		return type;
	}
	
	public void PrintWord(){
		System.out.println(word+" <"+ReadableWordType(wordType)+">");
		for (int i = 0; i < this.length; i++) {
			System.out.println(word.charAt(i)+", "+bopomofo[i]+", "+String.valueOf(tone[i]));
		}
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(word+" <"+ReadableWordType(wordType)+">\n");
		for (int i = 0; i < this.length; i++) {
			sb.append(word.charAt(i)+", "+bopomofo[i]+", "+String.valueOf(tone[i])+"\n");
		}
		return sb.toString();
	}
}
