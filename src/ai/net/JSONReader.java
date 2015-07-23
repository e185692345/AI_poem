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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ai.exception.BopomofoException;
import ai.word.ChineseWord;

public class JSONReader {
	
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
	public static ChineseWord[] GetWordList(String topic){
		final int limit = 1000;
		ChineseWord[] tempWordList = new ChineseWord[limit];
		int wordType,count = 0;
		
		try {
			String word,startOrEnd;
			//String url = new String("http://conceptnet5.media.mit.edu/data/5.3/c/zh/"+URLEncoder.encode(topic,"UTF-8")+"?limit="+limit);
			JSONObject obj, jsonObj;
			
			String url = new String("http://conceptnet5.media.mit.edu/data/5.2/search?limit="+limit+"&nodes=/c/zh_TW/"+URLEncoder.encode(topic,"UTF-8"));
			System.out.println(url);
			obj = ReadJsonFromURL(url);
			
			JSONArray array = null;
			try {
				array = obj.getJSONArray("edges");
				for (int i=0; i<array.length(); i++){
					jsonObj  = ((JSONObject)array.getJSONObject(i));
					if (jsonObj.get("start").toString().indexOf(topic) != -1){
						word = jsonObj.getString("end").split("/")[3];
						startOrEnd = new String("end");
					}
					else if (jsonObj.get("end").toString().indexOf(topic) != -1){
						word = jsonObj.getString("start").split("/")[3];
						startOrEnd = new String("start");
					}
					else{
						System.err.println("error : ConceptNet gives a json object without corresponding start wrod / end word");
						continue;
					}
					
					if (word.length() <= 3){
						wordType = GetWordType(jsonObj.getString("rel"),startOrEnd);
						try {
							tempWordList[count] =  new ChineseWord(word, HtmlReader.GetBopomofo(word), wordType);
							count += 1;
						} catch (BopomofoException e) {
							// TODO Auto-generated catch block
							/*e.printStackTrace();*/
						}
						
					}
				}
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ChineseWord[] wordList = new ChineseWord[count];
		for(int i = 0 ; i < count ; i++){
			wordList[i] = tempWordList[i];
		}
		return wordList;
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
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				FileOutputStream fout = new FileOutputStream("json.txt");
				BufferedOutputStream bufferFout = new BufferedOutputStream(fout);
				bufferFout.write(jsonStr.getBytes());
				fout.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if (is != null){
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
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
	private static int GetWordType(String relation, String startOrEnd){
		final String[] rel = new String[] {"/r/RelatedTo","/r/IsA","/r/PartOf","/r/HasA","/r/UsedFor","/r/CapableOf","/r/AtLocation","/r/Causes","/r/HasSubevent","/r/HasFirstSubevent","/r/HasPrerequisite","/r/HasProperty","/r/MotivatedByGoal","/r/Desires","/r/CreatedBy","/r/Synonym","/r/Antonym","/r/DerivedFrom","/r/MadeOf"};
		final String[] start = new String[] {"名形動","名","名","名","名","名","名","名動","動","動","動","名","動","名","名","名","名","名","名"};
		final String[] end = new String[] {"名形動","名","名","名","動","動","名","形動","動","動","動","形","名形動","名動","名","名","名","名","名"};
		int wordType = 0;
		String[] type = start;
		
		if (startOrEnd.equals("start"))
			type = start;
		else if (startOrEnd.equals("end"))
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
}
