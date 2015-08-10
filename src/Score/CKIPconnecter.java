package Score;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class CKIPconnecter
{
	private void WriteFile(ArrayList<String> data, int id)
	{
		BufferedWriter bufWriter;
		File inputFile = new File("CKIPClient/CKIPClient/test-utf-8/in/"+ id + ".txt");
		try {
			bufWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(inputFile,false),"UTF-8"));
	        for(int i = 0;i < data.size(); i++)
	        {
	        	try {
		            bufWriter.write(data.get(i));
		            bufWriter.newLine();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        try{
	            bufWriter.close();
	        } catch (IOException e) {
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}	
	private ArrayList<String> ReadFile(int id)
	{
		InputStreamReader read;
		ArrayList<String> parts_of_speech = new ArrayList<String>();
		File outputFile = new File("CKIPClient/CKIPClient/test-utf-8/out/" + id +".txt");
		int count=0;
		while( !outputFile.exists()){	//wait until file exist
			try {
				System.out.println("waiting for " + id + ".txt");
				Thread.sleep(3000);
				count++;
				if(count>=50)
				{
					System.out.println("timeout");
					break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		try {
			read = new InputStreamReader (new FileInputStream(outputFile),"UTF-8");
			BufferedReader br=new BufferedReader(read);
			String str;
			try {
				while((str=br.readLine())!=null)
				{
					//System.out.println(str);
					String[] AfterSplit=str.split("[()]+");
					parts_of_speech.add(AfterSplit[1]);
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		return parts_of_speech;
	}
	public ArrayList<String> process(ArrayList<String> data, int id)
	{
		CMD cmd=new CMD();
		WriteFile(data,id);
		cmd.submit();
		ArrayList<String> parts_of_speech = ReadFile(id);
		return parts_of_speech;
	}
	public void test(int id)	//used for debug
	{
		ArrayList<String> data = new ArrayList<String>();
		data.add("人");
		data.add("狗");
		data.add("跳");
		ArrayList<String> parts_of_speech = process(data,id);
		for(int i=0;i<parts_of_speech.size();i++)
			System.out.println(data.get(i) + ": " + parts_of_speech.get(i));
		System.out.println("test finish");
	}
	public void output(ArrayList<String> input)
	{
		for(int i=0;i<input.size();i++)
			System.out.println(input.get(i));
	}
}
