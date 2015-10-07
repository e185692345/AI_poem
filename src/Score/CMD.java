package Score;
public class CMD {
	 public void submit() {
	     //String bat="run.bat";
	     Runtime run = Runtime.getRuntime();
	     //String str1 = "cd CKIPClient\\CKIPClient";
	     String str2 = "java -jar CKIPClient/CKIPClient/CKIPClient.jar CKIPClient/CKIPClient/ckipsocket-utf-8.propeties CKIPClient/CKIPClient/test-utf-8/in CKIPClient/CKIPClient/test-utf-8/out";
	     //ButtonPressSimulator Press=new ButtonPressSimulator();
	     String[] cmd = {
	    		 "/bin/sh",
	    		 "-c",
	    		 str2
	    		 };
	     try {
	    	//run.exec("cmd /c run.bat");
	    	//run.exec("cmd /c strrt " + str1);
	    	if(System.getProperty("os.name").charAt(0) == 'L')
	    		run.exec(cmd);
	    	if(System.getProperty("os.name").charAt(0) == 'W')
	    		run.exec("cmd /c " + str2);
	    	//Thread.sleep(1000);
	    	//Press.PressEnter();
	     } catch (Exception e) {            
	         e.printStackTrace(); 
	     }   
	 }
}
