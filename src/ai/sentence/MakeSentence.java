package ai.sentence;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import ai.exception.MakeSentenceException;
import ai.exception.RelationWordException;
import ai.word.ChineseWord;
import ai.word.Relation;
import ai.word.WordPile;

public class MakeSentence {
	// TODO 補齊所有句型， data檔要增加格式說明
	public static final int sentenceType0 = 0;	// /r/HasProperty + 的 + /r/IsA
	public static final int sentenceType1 = 1;	// 在 + /r/AtLocation + /r/CapableOf
	public static final int sentenceType2 = 2;	// TOPIC + 會/想 + /r/CapableOf
	public static final int sentenceType3 = 3;	// TOPIC + 是 + /r/IsA
	public static final int sentenceType4 = 4;	// 可以 + /r/CapableOf
	
	private final static String paddingWordFile = "paddingWord.data";
	private final static String sentenceTypeFile = "sentenceType.data";
	
	private static final String TOPIC = "TOPIC";
	private static final String PADDIND = "PADDING";
	
	private Random rand = new Random();
	private WordPile wordPile;
	private String[][][] sentenceTemplate;
	private int countType = 0;

	/**
	 * 從 paddingWordFile 和 sentenceTypeFile 分別載入
	 * @param wordPile
	 * @throws Exception 
	 */

	public MakeSentence(WordPile wordPile) {
		this.wordPile = wordPile;
		int countType1 = loadSentenceTypeFile();
		int countType2 = loadPaddingWordFile();
		if (countType1 != countType2 && countType1 != 0){
			System.err.println(paddingWordFile+" 和 "+sentenceTypeFile+" 紀錄的模板數量不一樣");
			System.exit(1);
		}
		else{
			countType = countType1;
		}
	}
	
	private int loadSentenceTypeFile(){
		try {
			BufferedReader bufRead = new BufferedReader(new FileReader(sentenceTypeFile));
			int countType = Integer.parseInt(bufRead.readLine());
			
			sentenceTemplate = new String[countType][][];
			for (int i = 0 ; i < countType ; i++){
				int countTemplate = Integer.parseInt(bufRead.readLine());
				sentenceTemplate[i] = new String[countTemplate][];
				for (int j = 0 ; j < countTemplate ; j++){
					sentenceTemplate[i][j] = bufRead.readLine().split(" +");
				}
			}
			bufRead.close();
			return countType;
		} catch (IOException e) {
			sentenceTemplate = null;
			e.printStackTrace();
			return 0;
		}
	}
	
	private int loadPaddingWordFile(){
		String[] str;
		try {
			BufferedReader bufRead = new BufferedReader(new FileReader(paddingWordFile));
			int countType = Integer.parseInt(bufRead.readLine());
			ChineseWord[][] paddingWordList = new ChineseWord[countType][];
			for ( int i = 0 ; i < countType ; i++){
				int countWord = Integer.valueOf(bufRead.readLine());
				paddingWordList[i] = new ChineseWord[countWord];
				for ( int j = 0 ; j < countWord ; j++){
					str = bufRead.readLine().split(" +");
					paddingWordList[i][j] = new ChineseWord(str[0],Arrays.copyOfRange(str, 1,str.length), ChineseWord.PADDING, Relation.PADDING, Relation.START);
				}
			}
			wordPile.setPaddindWordList(paddingWordList);
			bufRead.close();
			return countType;
		} catch (IOException e) {
			wordPile.setPaddindWordList(null);
			e.printStackTrace();
			return 0;
		}
	}

	public PoemSentence makeRandomSentence() throws MakeSentenceException {
		int index = rand.nextInt(countType);
		for ( int i = 0 ; i < countType ; i++){
			try {
				return makeSentence((index+i)%countType);
			} catch (MakeSentenceException e) {
				continue;
			}
		}
		throw new MakeSentenceException(-1);
	}
	public PoemSentence makeSentence(int type) throws MakeSentenceException{
		int index = rand.nextInt(LineComposition.FIVE_LETTER_COMPOSITION.length);
		
		for (int i = 0 ; i < LineComposition.FIVE_LETTER_COMPOSITION.length ; i++){
			int[] composition = LineComposition.FIVE_LETTER_COMPOSITION[(i+index)%LineComposition.FIVE_LETTER_COMPOSITION.length];
			for ( int j = 0 ; j < sentenceTemplate[type].length ; j++){
				if ( sentenceTemplate[type][j].length != composition.length)
					continue;
				ChineseWord[] words = new ChineseWord[composition.length];
				boolean isDone = true;
				for ( int k = 0 ; k < sentenceTemplate[type][j].length ; k++){
					if (sentenceTemplate[type][j][k].equals(TOPIC)){
						words[k] = wordPile.getTopic();
						if ( composition[k] != words[k].getLength()){
							isDone = false;
							break;
						}
					}
					else if (sentenceTemplate[type][j][k].indexOf(PADDIND) != -1){
						int paddingWordLength = Integer.parseInt(sentenceTemplate[type][j][k].split("_")[1]);
						if (composition[k] == paddingWordLength){
							words[k] = wordPile.getAPaddingWord(type);
						}
						else{
							isDone = false;
							break;
						}
					}
					else{
						String[] data = sentenceTemplate[type][j][k].split("_");
						int relation = Relation.getRelationID(data[0]);
						int startOrEnd = Integer.parseInt(data[1]);
						try {
							words[k] = wordPile.getRlationWord(relation, startOrEnd, composition[k]);
						} catch (RelationWordException e) {
							isDone = false;
							break;
						}
					}
				}
				if (isDone){
					return new PoemSentence(type,words);
				}
			}
		}
		throw new MakeSentenceException(type);
	}
	
}
