package ai.sentence;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import ai.exception.MakeSentenceException;
import ai.word.ChineseWord;
import ai.word.Relation;
import ai.word.WordPile;

public class MakeSentence {
	public static final int sentenceType0 = 0;	// /r/HasProperty + 的 + /r/IsA
	public static final int sentenceType1 = 1;	// 在 + /r/AtLocation + /r/CapableOf
	public static final int sentenceType2 = 2;	// TOPIC + 會/想 + /r/CapableOf
	public static final int sentenceType3 = 3;	// TOPIC + 是 + /r/IsA
	public static final int sentenceType4 = 4;	// 可以 + /r/CapableOf
	
	private final static String paddingWordFile = "paddingWord.txt";
	private final static String sentenceTypeFile = "sentenceType.txt";
	
	private static final String TOPIC = "TOPIC";
	private static final String PADDIND = "PADDING";
	
	private Random rand = new Random();
	private WordPile wordPile;
	private String[][][] sentenceTemplate;
	
	/**
	 * 從 paddingWordFile 和 sentenceTypeFile 分別載入
	 * @param wordPile
	 */
	public MakeSentence(WordPile wordPile) {
		this.wordPile = wordPile;
		LoadSentenceType();
		LoadPaddingWord();
	}
	
	private void LoadSentenceType(){
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
		} catch (IOException e) {
			sentenceTemplate = null;
			e.printStackTrace();
		}
	}
	
	private void LoadPaddingWord(){
		String[] str;
		int countType;
		try {
			BufferedReader bufRead = new BufferedReader(new FileReader(paddingWordFile));
			countType = Integer.parseInt(bufRead.readLine());
			ChineseWord[][] paddingWordList = new ChineseWord[countType][];
			for ( int i = 0 ; i < countType ; i++){
				int countWord = Integer.valueOf(bufRead.readLine());
				paddingWordList[i] = new ChineseWord[countWord];
				for ( int j = 0 ; j < countWord ; j++){
					str = bufRead.readLine().split(" +");
					paddingWordList[i][j] = new ChineseWord(str[0],Arrays.copyOfRange(str, 1,str.length), ChineseWord.padding, Relation.ELSE, Relation.START);
				}
			}
			wordPile.setPaddindWordList(paddingWordList);
			bufRead.close();
		} catch (IOException e) {
			wordPile.setPaddindWordList(null);
			e.printStackTrace();
		}
	}

	

	public PoemSentence makeSentence(int type) throws MakeSentenceException{
		int index = rand.nextInt(LineComposition.fiveLetterComposition.length);
		
		for (int i = 0 ; i < LineComposition.fiveLetterComposition.length ; i++){
			int[] composition = LineComposition.fiveLetterComposition[(i+index)%LineComposition.fiveLetterComposition.length];
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
						int relation = Relation.GetRelationID(data[0]);
						int startOrEnd = Integer.parseInt(data[1]);
						ChineseWord word = wordPile.GetRlationWord(relation, startOrEnd, composition[k]);
						if (word == null){
							isDone = false;
							break;
						}
						else{
							words[k] = word;
						}
					}
				}
				if (isDone){
					return new PoemSentence(type,words);
				}
			}
		}
		System.err.println("Fail To Make Sentence");
		throw new MakeSentenceException(type);
	}
	
}
