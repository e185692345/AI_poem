package ai.net;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

import ai.exception.BopomofoException;
import ai.word.ChineseWord;
import ai.word.Relation;

public class JSONReader {
	
	private HashMap<String, Boolean> isRecorded;
	private String topic;
	
	public JSONReader(String topic) {
		this.topic = topic;
		isRecorded = new HashMap<String,Boolean>();
	}
	/**
	 * 	利用concept net的API尋找和某個主題相關的詞，並且利用rel來推測詞的詞性
	 * 	@param  topic: 某個主題(String)
	 * 	@return 相關詞的陣列(ChineseWord[])
	 * 	
	 * 	預設回傳500筆資料，但是有的詞會找不到對應的注音，
	 * 	而且如果某個詞的字多於3個 或是 某個詞無法定義其詞性，則該筆資料也會被忽略
	 * 	所以結果通常會少於500筆
	 * 
	 * 	處理json的library要另外去下載，請參考 http://www.ewdna.com/2008/10/jsonjar.html
	 */
	public ChineseWord[] GetWordList(){
		final int limit = 9999;
		ChineseWord[] tempWordList = new ChineseWord[limit];
		int wordType,count = 0, relationID;
		
		try {
			String word;
			int startOrEnd;
			//String url = new String("http://conceptnet5.media.mit.edu/data/5.3/c/zh/"+URLEncoder.encode(topic,"UTF-8")+"?limit="+limit);
			JSONObject obj, jsonObj;
			
			String url = new String("http://conceptnet5.media.mit.edu/data/5.2/search?limit="+limit+"&nodes=/c/zh_TW/"+URLEncoder.encode(topic,"UTF-8"));
			System.out.println(url);
			obj = ReadJsonFromURL(url);
			
			try {
				JSONArray array = obj.getJSONArray("edges");
				for (int i=0; i<array.length(); i++){
					jsonObj  = ((JSONObject)array.getJSONObject(i));
					relationID = Relation.GetRelationID(jsonObj.getString("rel"));
					if (jsonObj.get("start").toString().indexOf(topic) != -1){
						word = jsonObj.getString("end").split("/")[3];
						startOrEnd = Relation.END;
					}
					else if (jsonObj.get("end").toString().indexOf(topic) != -1){
						word = jsonObj.getString("start").split("/")[3];
						startOrEnd = Relation.START;
					}
					else{
						System.err.println("warning : ConceptNet gives a json object without corresponding start wrod / end word");
						continue;
					}
					
					if (word.length() <= 3 && relationID != -1){
						if (!isRecorded.containsKey(word)){
							isRecorded.put(word, true);
							wordType = GetWordType(jsonObj.getString("rel"),startOrEnd);
							try {
								tempWordList[count] =  new ChineseWord(word, HtmlReader.GetBopomofo(word), wordType, relationID, startOrEnd);
								count += 1;
							} catch (BopomofoException e) {
								System.err.println(e.getMessage());
								continue;	
							}
						}
					}
				}
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return Arrays.copyOf(tempWordList, count);
	}
	
	public ChineseWord[] GetWordList_UsingEnlishSource(){
		final int limit = 10;	
		String englishTopic;
		
		Translate.setClientId(MicrosoftTranslatorKey.ID);
		Translate.setClientSecret(MicrosoftTranslatorKey.SECRET);
		try {
			englishTopic = Translate.execute(topic, Language.CHINESE_TRADITIONAL, Language.ENGLISH).toLowerCase();
		} catch (Exception e1) {
			englishTopic = "";
			e1.printStackTrace();
		}
		
		String url = new String("http://conceptnet5.media.mit.edu/data/5.2/search?limit="+limit+"&nodes=/c/en/"+englishTopic);
		System.out.println(url);
		JSONObject obj = ReadJsonFromURL(url);
		
		try {
			JSONArray array = obj.getJSONArray("edges");
			String[] englishInput = new String[limit];
			SemiChineseWord[] semiWordData = new SemiChineseWord[limit];
			int countTranlation = 0;
			
			for (int i=0; i<array.length(); i++){
				JSONObject jsonObj  = ((JSONObject)array.getJSONObject(i));
				String word;
				int startOrEnd;
				int relationID = Relation.GetRelationID(jsonObj.getString("rel"));
				int wordType;
				String startWord = jsonObj.get("start").toString().substring(6);
				String endWord = jsonObj.getString("end").toString().substring(6);
				
				startWord = startWord.replace('_', ' ');
				endWord = endWord.replace('_', ' ');
				if (startWord.equals(englishTopic)){
					word = endWord;
					startOrEnd = Relation.END;
				}
				else if (endWord.equals(englishTopic)){
					word = startWord;
					startOrEnd = Relation.START;
				}
				else{
					System.err.println("warning : ConceptNet gives a json object without corresponding start wrod / end word");
					continue;
				}
				wordType = GetWordType(jsonObj.getString("rel"),startOrEnd);
				
				if (word.split(" ").length <= 3 && relationID != -1){
					if (!isRecorded.containsKey(word)){
						isRecorded.put(word, true);
						englishInput[countTranlation] = word;
						semiWordData[countTranlation] = new SemiChineseWord(wordType, relationID, startOrEnd);
						countTranlation += 1;
					}
				}		
			}
			
			
			
			String[] chineseOutput = new String[0];
			try {
				chineseOutput = Translate.execute(Arrays.copyOf(englishInput, countTranlation),Language.ENGLISH,Language.CHINESE_TRADITIONAL);
			} catch (Exception e) {
				e.printStackTrace();
			}
			for (int i = 0 ; i < countTranlation ; i++)
				System.out.println(englishInput[i] + " => " + chineseOutput[i]);
			ChineseWord[] tempWordList = new ChineseWord[limit]; 
			int count = 0;
			for (int i = 0 ; i < countTranlation && i < chineseOutput.length ; i++){
				if ( englishInput[i].equals(chineseOutput[i]) || chineseOutput[i].length() > 3)
					continue;
				try {
					SemiChineseWord data = semiWordData[i];
					tempWordList[count] =  new ChineseWord(chineseOutput[i], HtmlReader.GetBopomofo(chineseOutput[i]), data.wordType, data.relationID, data.startOrEnd);
					count += 1;
				} catch (BopomofoException e) {
					System.err.println(e.getMessage());
					continue;
				}
			}
			
			return Arrays.copyOf(tempWordList, count);
		} catch (JSONException e) {
			e.printStackTrace();
			return new ChineseWord[0];
		}
	}
	
	/**
	 * 
	 * 	@param  url: json檔案的url (String)
	 * 	@return JSONObject
	 */
	private static JSONObject ReadJsonFromURL(String url){
		JSONObject json = null;
		InputStream is = null;
		try {
			int c;
			is = new URL(url).openStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,Charset.forName("UTF-8")));
			StringBuilder strBuilder = new StringBuilder();
			while( (c = reader.read()) != -1){
				strBuilder.append((char)c);
			}
			String jsonStr = strBuilder.toString();
			try {
				json = new JSONObject(jsonStr);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			try {
				FileOutputStream fout = new FileOutputStream("json.txt");
				BufferedOutputStream bufferFout = new BufferedOutputStream(fout);
				bufferFout.write(jsonStr.getBytes());
				fout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (is != null){
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return json;
	}
	
	/**
	 * 	利用relation推測詞的詞性
	 * 	@param relation: concept net 上定義的 relation
	 *  @param startOrEnd: 詞是在 start 還是 end
	 *  @return 一個代表詞性的數字，參見ai.word.ChineseWord (int)
	 *  
	 *  如果遇到未知的 relation 則會回傳 0，表示沒有任何詞性
	 */
	private static int GetWordType(String relation, int startOrEnd){
		final String[] rel = new String[] {"/r/RelatedTo","/r/IsA","/r/PartOf","/r/HasA","/r/UsedFor","/r/CapableOf","/r/AtLocation","/r/Causes","/r/HasSubevent","/r/HasFirstSubevent","/r/HasPrerequisite","/r/HasProperty","/r/MotivatedByGoal","/r/Desires","/r/CreatedBy","/r/Synonym","/r/Antonym","/r/DerivedFrom","/r/MadeOf"};
		final String[] start = new String[] {"名形動","名","名","名","名","名","名","名動","動","動","動","名","動","名","名","名","名","名","名"};
		final String[] end = new String[] {"名形動","名","名","名","動","動","名","形動","動","動","動","形","名形動","名動","名","名","名","名","名"};
		int wordType = 0;
		String[] type = start;
		
		if (startOrEnd == Relation.START) // start : 0
			type = start;
		else if (startOrEnd == Relation.END) // end : 1
			type = end;
		else {
			System.err.println("error : the second argument of GetWordType can only be \"start\" or \"end\"");
			System.exit(1);
		}
		for (int i = 0 ; i < rel.length ; i++){
			if (relation.equals(rel[i])){
				if (type[i].indexOf("名") != -1){
					wordType += ChineseWord.noun;
				}
				if (type[i].indexOf("形") != -1){
					wordType += ChineseWord.adj;	
				}
				if (type[i].indexOf("動") != -1){
					wordType += ChineseWord.verb;
				}
				break;
			}
		}
		
		return wordType;
	}
	
	/**
	 * 儲存除了 word 和 bopomofo 以外其他 ChineseWord的必要元素
	 */
	private class SemiChineseWord{
		int wordType;
		int relationID;
		int startOrEnd;
		
		public SemiChineseWord(int wordType, int relationID, int startOrEnd) {
			this.wordType = wordType;
			this.relationID = relationID;
			this.startOrEnd = startOrEnd;
		}
	}
}
