package ai.net;

import java.io.BufferedReader;
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
import ai.exception.RelationConvertException;
import ai.word.ChineseWord;
import ai.word.Relation;

public class ConceptNetCrawler {
	
	private static final int LIMIT = 9999;
	private static final int MAX_TRANSLATION = 1000;
	
	
	private String topic;
	
	public ConceptNetCrawler(String topic) {
		this.topic = topic;
	}
	/**
	 * 	利用concept net的API尋找和某個中文主題相關的詞，並且利用rel來推測詞的詞性
	 * 	@return 相關詞的陣列(ChineseWord[])
	 * 	
	 * 	並非所有從concept net上獲得的詞語都可以轉換成詞庫
	 * 	有的詞會找不到對應的注音，有的詞的字多於3個 或是 某個詞無法定義其詞性，則該筆資料會被忽略
	 * 	程式會用Hash的放式避免同一個詞語重複被收入詞庫
	 * 
	 * 	處理json的library要另外去下載，請參考 http://www.ewdna.com/2008/10/jsonjar.html
	 */
	public ChineseWord[] getWordList_ChineseSource(){
		ChineseWord[] tempWordList = new ChineseWord[LIMIT];
		int wordType,count = 0;
		Relation relation;
		HashMap<String, Boolean> isRecorded = new HashMap<String,Boolean>();
		try {
			String word;
			int startOrEnd;
			//String url = new String("http://conceptnet5.media.mit.edu/data/5.3/c/zh/"+URLEncoder.encode(topic,"UTF-8")+"?limit="+limit);
			JSONObject obj, jsonObj;
			
			String url = new String("http://conceptnet5.media.mit.edu/data/5.2/search?limit="+LIMIT+"&nodes=/c/zh_TW/"+URLEncoder.encode(topic,"UTF-8"));
			System.out.println(url);
			obj = readJsonFromURL(url);
			
			try {
				JSONArray array = obj.getJSONArray("edges");
				for (int i=0; i<array.length(); i++){
					jsonObj  = ((JSONObject)array.getJSONObject(i));
					try {
						relation = Relation.getRelation(jsonObj.getString("rel"));
						String startWord = jsonObj.getString("start").split("/")[3];
						String endWord = jsonObj.getString("end").split("/")[3];
						if (startWord.equals(topic)){
							word = endWord;
							startOrEnd = Relation.END;
						}
						else if (endWord.equals(topic)){
							word = startWord;
							startOrEnd = Relation.START;
						}
						else{
							//System.err.println("warning : ConceptNet gives a json object without corresponding start wrod / end word ( "+startWord+" , "+endWord+" )");
							continue;
						}
						
						if (word.length() <= 3){
							if (!isRecorded.containsKey(word)){
								isRecorded.put(word, true);
								wordType = Relation.getWordType(relation,startOrEnd);
								try {
									tempWordList[count] =  new ChineseWord(word, BopomofoCrawler.getBopomofo(word), wordType, relation, startOrEnd);
									count += 1;
								} catch (BopomofoException e) {
									System.err.println(e.getMessage());
									continue;	
								}
							}
							else{
								//System.out.println(word + " 已經出現過");
							}
						}
					} catch (RelationConvertException e1) {
						System.err.println(e1.getMessage());
						//e1.printStackTrace();
						continue;
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
	
	/**
	 * 先把主題翻成英文再去concept net上找相關詞，之後再把相關詞翻譯成中文
	 * @return 相關詞的陣列(ChineseWord[])
	 * 
	 * 	並非所有從concept net上獲得的詞語都可以轉換成詞庫
	 * 	有的詞會找不到對應的注音，有的詞的字多於3個 或是 某個詞無法定義其詞性，則該筆資料會被忽略
	 * 	程式會用Hash的放式避免同一個詞語重複被收入詞庫
	 * 
	 * 翻譯是利用 Microsoft translator API 進行的，請參考 : https://msdn.microsoft.com/en-us/library/hh454949.aspx
	 * Java 的翻譯接口 API 是由第三方撰寫，請參考 : https://code.google.com/p/microsoft-translator-java-api/
	 *	
	 *	[重要] 如果相關詞由多個英文單字組成，單字之間會以底線區隔，進行翻譯之前要把底線換成空白
	 *	[重要] 中文翻成英文時第一個字母會是大寫，要把它轉換成小寫才可以順利在concept net上取得資料
	 */
	public ChineseWord[] getWordList_EnlishSource(){
		String englishTopic;
		HashMap<String, Boolean> isRecorded = new HashMap<String,Boolean>();
		Translate.setClientId(MicrosoftTranslatorKey.ID);
		Translate.setClientSecret(MicrosoftTranslatorKey.SECRET);
		try {
			englishTopic = Translate.execute(topic, Language.CHINESE_TRADITIONAL, Language.ENGLISH).toLowerCase();
		} catch (Exception e1) {
			englishTopic = "";
			e1.printStackTrace();
		}
		
		String url = new String("http://conceptnet5.media.mit.edu/data/5.2/search?limit="+LIMIT+"&nodes=/c/en/"+englishTopic);
		System.out.println(url);
		JSONObject obj = readJsonFromURL(url);
		
		try {
			JSONArray array = obj.getJSONArray("edges");
			String[] englishInput = new String[LIMIT];
			SemiChineseWord[] semiWordData = new SemiChineseWord[LIMIT];
			int countTranlation = 0;
			
			for (int i=0; i<array.length(); i++){
				JSONObject jsonObj  = ((JSONObject)array.getJSONObject(i));
				
				try {
					String word;
					int startOrEnd;
					Relation relation = Relation.getRelation(jsonObj.getString("rel"));
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
						//System.err.println("warning : ConceptNet gives a json object without corresponding start wrod / end word ( "+startWord+" , "+endWord+" )");
						continue;
					}
					wordType = Relation.getWordType(relation,startOrEnd);
					
					if (word.split(" ").length <= 3){
						if (!isRecorded.containsKey(word)){
							isRecorded.put(word, true);
							englishInput[countTranlation] = word;
							semiWordData[countTranlation] = new SemiChineseWord(wordType, relation, startOrEnd);
							countTranlation += 1;
						}
						else{
							//System.out.println(word + " 已經出現過");
						}
					}	
				} catch (RelationConvertException e) {
					System.err.println(e.getMessage());
					//e.printStackTrace();
					continue;
				}
					
			}
			
			/*限制一次翻譯詞語數上限，避免太快把每個月的翻譯配額用完*/
			if (countTranlation > MAX_TRANSLATION)
				countTranlation = MAX_TRANSLATION;
			
			String[] chineseOutput = new String[countTranlation],tmp;
			/*把大量的翻譯分成小份的分批執行，不然會有Error*/
			final int slice = 200;
			try {
				int offset = countTranlation % slice;
				if (offset > 0){
					tmp = Translate.execute(Arrays.copyOf(englishInput, offset),Language.ENGLISH,Language.CHINESE_TRADITIONAL);
					System.arraycopy(tmp, 0, chineseOutput, 0, offset);
				}
				for (int i = offset ; i < countTranlation ; i += slice){
					tmp = Translate.execute(Arrays.copyOfRange(englishInput, i, i+slice), Language.ENGLISH,Language.CHINESE_TRADITIONAL);
					System.arraycopy(tmp, 0, chineseOutput, i, slice);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			System.out.println("共翻譯了 "+chineseOutput.length+" 個詞");
			ChineseWord[] tempWordList = new ChineseWord[LIMIT]; 
			int count = 0;
			for (int i = 0 ; i < countTranlation && i < chineseOutput.length ; i++){
				if ( englishInput[i].equals(chineseOutput[i]) || chineseOutput[i].length() > 3)
					continue;
				String word = chineseOutput[i];
				if (!isRecorded.containsKey(word)){
					try {
						SemiChineseWord data = semiWordData[i];
						tempWordList[count] =  new ChineseWord(word, BopomofoCrawler.getBopomofo(word), data.wordType, data.relation, data.startOrEnd);
						count += 1;
					} catch (BopomofoException e) {
						System.err.println(e.getMessage());
						continue;
					}
				}
				else{
					//System.out.println(word + " 已經出現過");
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
	private static JSONObject readJsonFromURL(String url){
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
	 * 儲存除了 word 和 bopomofo 以外其他 ChineseWord的必要元素
	 */
	private class SemiChineseWord{
		int wordType;
		Relation relation;
		int startOrEnd;
		
		public SemiChineseWord(int wordType, Relation relation, int startOrEnd) {
			this.wordType = wordType;
			this.relation = relation;
			this.startOrEnd = startOrEnd;
		}
	}
}
