package Concept;

import java.util.Scanner;
import java.net.MalformedURLException;
import java.net.URL;

public class Concept {
	
	public static String topic;
	public static void main(String[] args) {
		
		URL url = null;
		String str = new String();
		String tone = new String();
		String concept = new String("上");
		Scanner input = new Scanner(System.in);		 
		//topic = input.next();
		//System.out.println(topic);
		//Crawler crawler = new Crawler();
		try {
			//url = new URL("http://conceptnet5.media.mit.edu/data/5.3/c/zh/" + Crawler.encode(concept));
			url = new URL("http://www.chinese-artists.net/poem/?query=" + Crawler.encode(concept));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		str = Crawler.crawl(url);
		//System.out.println(str);
		//Json.parse(str);		
	}	
	
}
