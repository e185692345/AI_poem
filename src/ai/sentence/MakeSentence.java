package ai.sentence;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import ai.exception.MakeSentenceException;
import ai.exception.RelationConvertException;
import ai.exception.RelationWordException;
import ai.word.ChineseWord;
import ai.word.Relation;
import ai.word.WordPile;

public class MakeSentence {
	private final static String paddingWordFile = "paddingWord.data";
	private final static String sentenceTypeFile = "sentenceType.data";
	
	private Random rand = new Random();
	private WordPile wordPile;
	/*第一層: 第幾個句型, 第二層:第幾個element*/
	private String[][] sentenceTemplate;
	private int countType = 0;
	private HashMap<String, ChineseWord> paddingWordList = new HashMap<String, ChineseWord>();
	/**
	 * 從 paddingWordFile 和 sentenceTypeFile 分別載入
	 * @param wordPile
	 * @throws Exception 
	 */

	public MakeSentence(WordPile wordPile) {
		this.wordPile = wordPile;
		loadSentenceTypeFile();
		loadPaddingWordFile();
	}
	
	private void loadSentenceTypeFile(){
		try {
			countType = getFileLines(sentenceTypeFile);
			System.out.println("總共有 "+countType+" 個句型模板");
			BufferedReader bufRead = new BufferedReader(new FileReader(sentenceTypeFile));
			
			sentenceTemplate = new String[countType][];
			for (int i = 0 ; i < countType ; i++){
				sentenceTemplate[i] = bufRead.readLine().split(" +");
			}
			bufRead.close();
		} catch (IOException e) {
			sentenceTemplate = null;
			e.printStackTrace();
		}
	}
	
	private int getFileLines(String fileName){
		int count = 0;
		try {
			BufferedReader bufRead = new BufferedReader(new FileReader(fileName));
			while (bufRead.readLine() != null){
				count += 1;
			}
			bufRead.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}
	
	private void loadPaddingWordFile(){
		try {
			BufferedReader bufRead = new BufferedReader(new FileReader(paddingWordFile));
			while (true){
				String line = bufRead.readLine();
				if (line == null)
					break;
				String[] str = line.split(" +");
				ChineseWord word  = new ChineseWord(str[0],Arrays.copyOfRange(str, 1,str.length-1), ChineseWord.convertWordType(str[str.length-1]), Relation.PADDING, Relation.START);
				if (!paddingWordList.containsKey(str[0])){
					paddingWordList.put(str[0], word);
				}
				else{
					System.err.println("warning : "+str[0]+" 已經在檔案中出現過了");
				}
			}
			bufRead.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		/*for (ChineseWord word : paddingWordList.values()){
			System.out.println(word);
		}*/
	}
	
	public PoemSentence makeSentence(int[] composition, int type) throws MakeSentenceException {
		final String TOPIC = "TOPIC";
		boolean isDone = true;
		ChineseWord[] words = new ChineseWord[composition.length];
		for ( int k = 0 ; k < sentenceTemplate[type].length ; k++){
			String element = sentenceTemplate[type][k];
			/*填入主題*/
			if (element.equals(TOPIC)){
				words[k] = wordPile.getTopic();
				if ( composition[k] != words[k].getLength()){
					isDone = false;
					break;
				}
			}
			/*填入padding word*/
			else if (element.charAt(0) == '('){
				try {
					words[k] = getAPaddingWord(type,k);
					if ( composition[k] != words[k].getLength()){
						isDone = false;
						break;
					}
				} catch (Exception e) {
					System.err.println(e.getMessage());
					e.printStackTrace();
					System.exit(1);
				}
				
			}
			/*填入relation word*/
			else{
				String[] data = element.split("_");
				try {
					Relation relation = Relation.getRelation(data[0]);
					int startOrEnd = Relation.START;
					if (data[1].equals("START"))
						startOrEnd = Relation.START;
					else if (data[1].equals("END"))
						startOrEnd = Relation.END;
					else{
						System.err.println(data[1]+" 不是 \"START\" 也不是 \"END\"");
						System.exit(1);
					}
					try {
						words[k] = wordPile.getRlationWord(relation, startOrEnd, composition[k]);
					} catch (RelationWordException e) {
						isDone = false;
						break;
					}
				} catch (RelationConvertException e1) {
					System.err.println(e1.getMessage());
					e1.printStackTrace();
					System.exit(1);
				}	
			}
		}
		if (isDone){
			return new PoemSentence(type,words);
		}
		else
			throw new MakeSentenceException(composition,sentenceTemplate[type]);
	}
	
	public PoemSentence makeSentence(int[] composition) throws MakeSentenceException{
		int index = rand.nextInt(countType);
		
		for (int i = 0 ; i < countType ; i++){
			int type = (index+i)%countType;
			if ( sentenceTemplate[type].length != composition.length)
				continue;
			try {
				return makeSentence(composition, type);
			} catch (MakeSentenceException e) {
				continue;
			}
		}
		throw new MakeSentenceException(composition);
	}
	
	public PoemSentence makeSentence(int type) throws MakeSentenceException{
		int length = LineComposition.FIVE_LETTER_COMPOSITION.length;
		int index = rand.nextInt(length);
		
		for (int i = 0 ; i < length; i++){
			int[] composition = LineComposition.FIVE_LETTER_COMPOSITION[(index+i)%length];
			if ( sentenceTemplate[type].length != composition.length)
				continue;
			try {
				return makeSentence(composition, type);
			} catch (MakeSentenceException e) {
				continue;
			}
		}
		throw new MakeSentenceException(sentenceTemplate[type]);
	}
	
	public ChineseWord getAPaddingWord(int sentenceType, int elementIndex) throws Exception{
		String element = sentenceTemplate[sentenceType][elementIndex];
		String[] availablePaddingWords = element.substring(1, element.length()-1).split("/");
		String word = availablePaddingWords[rand.nextInt(availablePaddingWords.length)];
		if (paddingWordList.containsKey(word)){
			return  paddingWordList.get(word);
		}
		else{
			// TODO 事先檢查檔案格式
			throw new Exception(word+"沒有出現在 "+paddingWordFile+" 裡面");
		}
	}
	
	public ChineseWord getAPaddingWord(String word) throws Exception{
		if (paddingWordList.containsKey(word))
			return paddingWordList.get(word);
		else {
			throw new Exception("沒有 \""+word+"\" 這個paddingWord");
		}
	}
}
