package ai.net;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ai.exception.BopomofoException;

public class HtmlReader {
	/**
	 *	用教育部國語辭典查詢單詞的注音，再用通用表示法把注音取出來
	 *	@param  : 欲查詢的詞(String)
	 *	@return : 對應的注音(String[])
	 *
	 *	因查詢結果可能會有兩種不同的呈現結果，所以要用兩種不同的通用表示式(matcher1)去讀取
	 *	matcher2是用來從matcher1的結果中取出注音的部分
	 *	
	 *	查詢「風」時，第一個結果是「八面威風」，所以還必須從較長的單字中取出欲查詢的詞和對應的注音
	 *	注意：html原始碼中有的空白是"全形"空白
	 */
	 static String[] GetBopomofo(String target) throws BopomofoException{
		final int buffferSize = 4096;

		String data = new String();
		BufferedInputStream in = null;
		
		try {
			byte[] buffer = new byte[buffferSize];
			int count;
			StringBuilder builder = new StringBuilder();
			String url = "http://dict.revised.moe.edu.tw/cgi-bin/newDict/dict.sh?idx=dict.idx&cond="+URLEncoder.encode(target,"BIG5")+"&pieceLen=1&fld=1";
			in = new BufferedInputStream(new URL(url).openStream());
			while( (count = in.read(buffer, 0, buffferSize) ) != -1){
				builder.append(new String(buffer,0,count,"BIG5"));
			}
			data = builder.toString();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if (in != null){
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		String word = new String("");
		String bopomofo = new String("");
		int findTarget = 0;
		
		Matcher matcher1 = Pattern.compile("<a href=\"javascript:fetch\\(0\\)\">(.*)<\\/a><\\/td>\\n<td>(.*)<\\/td>").matcher(data);
		Matcher matcher2;
		
		if (matcher1.find()){
			matcher2 = Pattern.compile(">.+?<").matcher(matcher1.group());
			if (matcher2.find())
				word = matcher2.group();
			if (matcher2.find())
				bopomofo = matcher2.group();
			if (word.length() > 0 && bopomofo.length() > 0){
				findTarget = 1;
				word = word.substring(1, word.length()-1);
				bopomofo = bopomofo.substring(1, bopomofo.length()-1);
			}
		}
		else{
			matcher1 = Pattern.compile("<\\/span>　.+?<\\/td>").matcher(data);
			if (matcher1.find()){
				matcher2 = Pattern.compile("　.+?<").matcher(matcher1.group());
				if (matcher2.find()){
					findTarget = 1;
					word = target;
					bopomofo = matcher2.group();
					bopomofo = bopomofo.substring(1, bopomofo.length()-1);
				}
			}
		}
		if (findTarget == 0){
			throw new BopomofoException(target);
		}
		
		String[] bopomofoList,letterBopomofo;
		int offset = word.indexOf(target);
		bopomofoList = bopomofo.split("　");
		
		if ( word.length() != bopomofoList.length){
			throw new BopomofoException(word,word.length(), bopomofoList.length);
		}
		letterBopomofo = new String[target.length()];
		for ( int i = 0 ; i < target.length() ; i++){
			letterBopomofo[i] = bopomofoList[i+offset];
		}
		
		return letterBopomofo;
	}
	
	
}
