package Concept;

public class Tone {

	/**
	 * @param args
	 */
	public static String get_tone(String sentence) {
		
		String tone = new String();
		String prefix = new String("<td bgcolor=\"#F2F2F2\">");
		int start_index = sentence.indexOf(prefix);
		
		while(start_index > 0){
			tone = tone + sentence.substring(start_index+prefix.length(),start_index+prefix.length()+1);			
			start_index = sentence.indexOf(prefix,start_index + 290);			
		}		
		if(tone.length() == 2 && tone.charAt(0) == tone.charAt(1))
			tone = new String(tone.substring(1));
		return tone;
	}
}
