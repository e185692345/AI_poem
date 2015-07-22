package Concept;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Json {
	
	public static int numFound;	
	public static String[] context;
	public static String[] dataset;
	public static String[] end;
	//public static
	public static String[] id;
	public static String[] license;
	public static String[] rel;
	public static String[] source_uri;
	//public static
	public static String[] start;
	public static String[] surfaceText;
	public static String[] uri;
	public static double[] weight;
	
	public static void parse(String str){
		JSONObject json = null;
		try {
			json = new JSONObject(str);
		} catch (JSONException e) {			
			e.printStackTrace();
		}
		try {
			JSONArray jsonAr = json.getJSONArray("edges");
			//System.out.println(jsonOb.length());
			/*** ***/
			context = new String[jsonAr.length()];
			dataset = new String[jsonAr.length()];
			end = new String[jsonAr.length()];
			id = new String[jsonAr.length()];
			license = new String[jsonAr.length()];
			rel = new String[jsonAr.length()];
			source_uri = new String[jsonAr.length()];
			start = new String[jsonAr.length()];
			surfaceText = new String[jsonAr.length()];
			uri = new String[jsonAr.length()];
			weight = new double[jsonAr.length()];
			
			for(int i=0; i<jsonAr.length(); i++){
				
				JSONObject jsonItem = jsonAr.getJSONObject(i);
				context[i] = jsonItem.getString("context");
				dataset[i] = jsonItem.getString("dataset");
				end[i] = jsonItem.getString("end");
				id[i] = jsonItem.getString("id");
				license[i] = jsonItem.getString("license");
				rel[i] = jsonItem.getString("rel");
				source_uri[i] = jsonItem.getString("source_uri");
				start[i] = jsonItem.getString("start");
				surfaceText[i] = jsonItem.getString("surfaceText");
				uri[i] = jsonItem.getString("uri");
				weight[i] = jsonItem.getDouble("weight");				
				
			}		
			numFound = json.getInt("numFound");			
		} catch (JSONException e) {			
			e.printStackTrace();
		}
	}
	
	public static String[] get_context(){
		return context;		
	}
	public static String[] get_dataset(){
		return dataset;		
	}
	public static String[] get_end(){
		for(int i=0; i<numFound; i++){
			end[i] = new String(end[i].substring(6));
		}
		return end;		
	}
	public static String[] get_id(){
		return id;		
	}
	public static String[] get_license(){
		return license;		
	}
	public static String[] get_rel(){
		for(int i=0; i<numFound; i++){
			rel[i] = new String(rel[i].substring(3));
		}
		return rel;		
	}
	public static String[] get_source_uri(){
		return source_uri;		
	}
	public static String[] get_start(){
		for(int i=0; i<numFound; i++){
			start[i] = new String(start[i].substring(6));
		}
		return start;			
	}
	public static String[] get_surfaceText(){
		return surfaceText;		
	}
	public static String[] get_uri(){
		return uri;		
	}
	public static double[] get_weight(){
		return weight;		
	}
	public static int get_number(){
		return numFound;		
	}

}
