package Concept;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Json {
	
	private static int numFound;	
	private static ArrayList<String> context = new ArrayList<String>();
	private static ArrayList<String> dataset = new ArrayList<String>();
	private static ArrayList<String> end = new ArrayList<String>();
	//public static
	private static ArrayList<String> id = new ArrayList<String>();
	private static ArrayList<String> license = new ArrayList<String>();
	private static ArrayList<String> rel = new ArrayList<String>();
	private static ArrayList<String> source_uri = new ArrayList<String>();
	//public static
	private static ArrayList<String> start = new ArrayList<String>();
	private static ArrayList<String> surfaceText = new ArrayList<String>();
	private static ArrayList<String> uri = new ArrayList<String>();
	private static ArrayList<Double> weight = new ArrayList<Double>();;
	
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
			for(int i=0; i<jsonAr.length(); i++){				
				JSONObject jsonItem = jsonAr.getJSONObject(i);
				context.add(jsonItem.getString("context"));
				dataset.add(jsonItem.getString("dataset"));
				end.add(jsonItem.getString("end"));
				id.add(jsonItem.getString("id"));
				license.add(jsonItem.getString("license"));
				rel.add(jsonItem.getString("rel"));
				source_uri.add(jsonItem.getString("source_uri"));
				start.add(jsonItem.getString("start"));
				surfaceText.add(jsonItem.getString("surfaceText"));
				uri.add(jsonItem.getString("uri"));
				weight.add(jsonItem.getDouble("weight"));				
				
			}		
			numFound = json.getInt("numFound");			
		} catch (JSONException e) {			
			e.printStackTrace();
		}
	}
	
	public static ArrayList<String> get_context(){
		return context;		
	}
	public static ArrayList<String> get_dataset(){
		return dataset;		
	}
	public static ArrayList<String> get_end(){
		for(int i=0; i<numFound; i++)
			end.set(i, end.get(i).substring(6));
		
		return end;		
	}
	public static ArrayList<String> get_id(){
		return id;		
	}
	public static ArrayList<String> get_license(){
		return license;		
	}
	public static ArrayList<String> get_rel(){
		for(int i=0; i<numFound; i++)
			rel.set(i, rel.get(i).substring(3));
		
		return rel;		
	}
	public static ArrayList<String> get_source_uri(){
		return source_uri;		
	}
	public static ArrayList<String> get_start(){
		for(int i=0; i<numFound; i++){
			start.set(i, start.get(i).substring(6));
		}
		return start;			
	}
	public static ArrayList<String> get_surfaceText(){
		return surfaceText;		
	}
	public static ArrayList<String> get_uri(){
		return uri;		
	}
	public static ArrayList<Double> get_weight(){
		return weight;		
	}
	public static int get_number(){
		return numFound;		
	}

}
