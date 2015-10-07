package Score;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

public class ButtonPressSimulator {
        public static void keyPress(Robot r,int key){
                r.keyPress(key);
                r.keyRelease(key);
                r.delay(100);
        }
		public void PressEnter()
		{
			Robot robot;
			try {
				robot = new Robot();
				robot.delay(500);
				keyPress(robot,KeyEvent.VK_ENTER);
			} catch (AWTException e) {
				e.printStackTrace();
			} 
		}
}