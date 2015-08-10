package Concept;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Crawler {

	public static String crawl(URL url){	
		
		String str = new String();
		//StringBuffer sb = new StringBuffer(); 		
        int responsecode;
        HttpURLConnection urlConnection;
        BufferedReader reader;
        String line = null;
        
		try{            
            //url = new URL("http://conceptnet5.media.mit.edu/data/5.3/c/zh/" + encode());          
            urlConnection = (HttpURLConnection)url.openConnection();            
            responsecode = urlConnection.getResponseCode();
            if(responsecode == 200){                
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while((line=reader.readLine()) != null){
                    //System.out.println(line);
                	//sb.append(line);
                	str = str + line;
                }
            }
            else{
                System.out.println("fail：" + responsecode);
            }
        }
        catch(Exception e){
            System.out.println("fail：" + e);
        }
		//System.out.println(str);		
		return str;
	}
	
	public static String encode(String str){
		String UTF8 = null;
		try {
			UTF8 = URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {			
			e.printStackTrace();
		}		
		return UTF8;
	}
	
}
