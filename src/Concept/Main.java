package Concept;

import java.util.ArrayList;
import java.util.Scanner;
import java.net.MalformedURLException;
import java.net.URL;

public class Main {
	
	public static String topic;
	public static void main(String[] args) {
		
		URL url = null;
		String str = new String();
		String tone = new String();
		ArrayList<String> rhyme =  new ArrayList<String>();
		String concept = new String("著");
		Scanner input = new Scanner(System.in);		 
		//topic = input.next();
		//System.out.println(topic);
		
		/*
		try {
			//url = new URL("http://conceptnet5.media.mit.edu/data/5.3/c/zh/" + Crawler.encode(concept));
			url = new URL("http://www.chinese-artists.net/poem/?query=" + Crawler.encode(concept));
		} catch (MalformedURLException e) {			
			e.printStackTrace();
		}
		str = Crawler.crawl(url);
		System.out.println(str);
		*/
		rhyme = Tone.get_rhyme(concept);
		for(int i=0; i<rhyme.size(); i++)
			System.out.println(rhyme.get(i));
		//Json.parse(str);		
	}	
	
}