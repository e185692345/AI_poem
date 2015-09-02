package ai.userInterface;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JProgressBar;

public final class MyUtility {
	
	public static void enableComponents(Container container,boolean bool){
		
		for (Component c : container.getComponents()){
			c.setEnabled(bool);
			if (c instanceof Container){
				enableComponents((Container)c, bool);
			}
		}
	}
	
	public static class ProgressBarSetting implements Runnable{
		private JProgressBar bar;
		private int progress;
		
		public ProgressBarSetting(JProgressBar bar, int progress){
			this.bar = bar;
			this.progress = progress;
		}
		@Override
		public void run() {
			bar.setValue(progress);			
		}
		
	}
}
