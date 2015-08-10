package ai.word;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ai.exception.BopomofoException;
import ai.exception.RelationConvertException;
import ai.exception.RelationWordException;
import ai.net.BopomofoCrawler;

public class WordPile {
	
	private ArrayList<ArrayList<ArrayList<ArrayList<ChineseWord>>>>  relationPile = new ArrayList<ArrayList<ArrayList<ArrayList<ChineseWord>>>>();
	private ArrayList<ChineseWord[]> wordListPile;
	private int totalWord;
	private ChineseWord topic;
	private Random rand = new Random();
	private HashMap<String, Boolean> isRecord;
	
	public WordPile(String topic, int wordType) {
		initLsit();
		wordListPile = new ArrayList<ChineseWord[]>();
		totalWord = 0;
		isRecord = new HashMap<String, Boolean>();
		SetTopic(topic, wordType);
	}
	private void SetTopic(String topic, int wordType){
		try {
			this.topic = new ChineseWord(topic, BopomofoCrawler.getBopomofo(topic), wordType, Relation.TOPIC,Relation.START);
		} catch (BopomofoException e) {
			this.topic = null;
			e.printStackTrace();
		}
	}
	
	public ChineseWord getTopic(){
		return topic;
	}
	
	public void AddWords(ChineseWord[] wordList){
		wordListPile.add(wordList);
		
		for (ChineseWord word : wordList){
			if(!isRecord.containsKey(word.getWord())){
				isRecord.put(word.getWord(), true);
				relationPile.get(word.getRelation().getIndex()).get(word.getStartOrEnd()).get(word.getLength()).add(word);
				totalWord += 1;
			}
		}
		System.out.printf("詞庫中新增了 %d 個詞 ， 目前共有 %d 個詞\n",wordList.length,totalWord);
	}
	
	public void addWords(JSONObject json){
		JSONArray arr = json.optJSONArray("wordPile");
		ChineseWord[] wordList = new ChineseWord[arr.length()];
		if ( arr != null){
			for ( int i = 0 ; i < arr.length() ; i++){
				JSONObject item;
				try {
					item = arr.getJSONObject(i);
					int relationIndex = Integer.parseInt(item.optJSONObject("relation").optString("index"));
					wordList[i] = new ChineseWord(item.optString("word"),getBopomofo(item.optJSONArray("bopomofo")), 
							getTone(item.optJSONArray("tone")),item.optInt("wordType"),item.optInt("length"),Relation.getRelation(relationIndex),item.optInt("startOrEnd"));
				} catch (JSONException e) {
					e.printStackTrace();
					continue;
				} catch (RelationConvertException e) {
					System.err.println(e.getMessage());
					e.printStackTrace();
					System.exit(1);
				}
				
				
			}
		}
		AddWords(wordList);
	}
	
	/**
	 * nounWord 儲存名詞，第 i 個 list 儲存長度是 i+1 的詞
	 * adjWord 是名詞，verbWord 存動詞，結構和 nounWord 相同
	 * 
	 * relationPile 則是把詞依照 relation 分類
	 * 第一層 index 依照 relation 分類
	 * 第二層 index 依照詞是在 star/end 分類
	 * 第三層 index 依照詞的長度分類 
	 */
	private void initLsit(){
		for ( int i = 0 ; i < Relation.TOTAL_RELATION ; i++){
			relationPile.add(new ArrayList<ArrayList<ArrayList<ChineseWord>>>());
			relationPile.get(i).add(new ArrayList<ArrayList<ChineseWord>>());
			relationPile.get(i).add(new ArrayList<ArrayList<ChineseWord>>());
			relationPile.get(i).get(0).add(new ArrayList<ChineseWord>());
			relationPile.get(i).get(0).add(new ArrayList<ChineseWord>());
			relationPile.get(i).get(0).add(new ArrayList<ChineseWord>());
			relationPile.get(i).get(0).add(new ArrayList<ChineseWord>());
			relationPile.get(i).get(1).add(new ArrayList<ChineseWord>());
			relationPile.get(i).get(1).add(new ArrayList<ChineseWord>());
			relationPile.get(i).get(1).add(new ArrayList<ChineseWord>());
			relationPile.get(i).get(1).add(new ArrayList<ChineseWord>());
			
		}
	}
	
	private int[] getTone(JSONArray arr){
		int[] tone = new int[arr.length()];
		for (int i = 0 ; i < arr.length() ; i++){
			tone[i] = arr.optInt(i);
		}
		
		return tone;
	}
	private char[] getBopomofo(JSONArray arr){
		char[] bopomofo = new char[arr.length()];
		for (int i = 0 ; i < arr.length() ; i++){
			bopomofo[i] = arr.optString(i).charAt(0);
		}
		return bopomofo;
	}
	
	public String getJSONString(){
		JSONObject json = new JSONObject();
		JSONArray arr = new JSONArray();
		for ( ChineseWord[] wordList : wordListPile){
			for ( ChineseWord word : wordList){
				JSONObject obj = new JSONObject(word);
				arr.put(obj);
			}
		}
		
		try {
			json.put("totalWord", totalWord);
			json.put("wordPile",arr);
		} catch (JSONException e) {
			e.printStackTrace();
			return "{}";
		}
		return json.toString();
	}
	
	
	/**
	 * 從 relationPile 中隨機取出一個符合條件的詞，若是沒有符合的詞則會回傳null
	 * 
	 * @param relation 參見ai.word.Relation
	 * @param startOrEnd start : 0 / end : 0
	 * @param length 詞的長度
	 * @return 若沒有符合的詞則會還傳null
	 * @throws RelationWordException 
	 */
	public ChineseWord getRlationWord(Relation relation,int startOrEnd, int length) throws RelationWordException{
		ArrayList<ChineseWord> list = relationPile.get(relation.getIndex()).get(startOrEnd).get(length);
		
		if (list.size() == 0){
			throw new RelationWordException(relation.getIndex(),length);
		}
		else{
			int index = rand.nextInt(list.size());
			return list.get(index);
		}
		
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("詞庫中共有 %d 個詞\n",totalWord));
		for (int i = 0 ; i < Relation.TOTAL_RELATION ; i++){
			try {
				sb.append(Relation.getRelation(i).toString()+"\n");
				sb.append("start : ");
				for ( int j = 1 ; j <= 3 ;j++){
					sb.append(relationPile.get(i).get(0).get(j).size());
					if ( j < 3)
						sb.append(" ,");
				}
				sb.append('\n');
				sb.append("end : ");
				for ( int j = 1 ; j <= 3 ;j++){
					sb.append(relationPile.get(i).get(1).get(j).size());
					if ( j < 3)
						sb.append(" ,");
				}
				sb.append('\n');
				sb.append('\n');
			} catch (RelationConvertException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
				System.exit(1);
			}
		}
		return sb.toString();
	}
}
