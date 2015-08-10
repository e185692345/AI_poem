package Concept;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Tone {
	
	private static URL url;
	private static String source_code;
	public static String get_tone(String word) {		
		
		get_source_code(word);
		
		String tone = new String();
		String pfx = new String("<td bgcolor=\"#F2F2F2\">");
		int st = source_code.indexOf(pfx);		
		int st_idx;
		while(st > 0){
			st_idx = st+pfx.length();
			tone = tone + source_code.substring(st_idx, st_idx+1);			
			st = source_code.indexOf(pfx, st+290);			
		}		
		if(tone.length() == 2 && tone.charAt(0) == tone.charAt(1))
			tone = new String(tone.substring(1));
		return tone;
	}
	public static ArrayList<String> get_rhyme(String word){
		
		get_source_code(word);
		
		ArrayList<String> rhyme =  new ArrayList<String>();
		String pfx = new String("<td bgcolor=\"#F2F2F2\">");
		String sfx = new String("</td>");
		String tmp_rhyme;
		int st = source_code.indexOf(pfx);
		int ed; 
		int st_idx;
		st = source_code.indexOf(pfx, st + 50);
		ed = source_code.indexOf(sfx, st);		
		
		while(st > 0){			
			st_idx = st + pfx.length();						
			tmp_rhyme = new String(source_code.substring(st_idx, ed));			
			rhyme.add(tmp_rhyme);
			st = source_code.indexOf(pfx, st + 290);
			ed = source_code.indexOf(sfx, st);
		}		
		return rhyme;
	}
	
	private static void get_source_code(String word){
		try {			
			url = new URL("http://www.chinese-artists.net/poem/?query=" + Crawler.encode(word));
		} 
		catch (MalformedURLException e) {			
			e.printStackTrace();
		}
		source_code = new String(Crawler.crawl(url));		 
	}
}
