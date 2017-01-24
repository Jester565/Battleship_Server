package pack;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public abstract class Core {
	public static boolean running = true;
	public static ScreenManager s;
	public static KeyInput keyListener;
	public static double rate;
	public static double timePassed;
	public static MouseInput mouseListener;
	public static ArrayList<ScreenNotification> notifications;
	public static void stop()
	{
		running = false;
		System.exit(0);
	}
	public void run(){
		try{
			init();
			gameLoop();
		}finally{
			s.restoreScreen();
			System.exit(0);
		}
	}
	//set to full screen
	public void init()
	{
		s = new ScreenManager();
		s.setToFullScreen();
		Window w = s.getScreenWindow();
		w.setFont(s.f);
		s.getGraphics().setFont(s.f);
		s.setBackGroundColor(.5f,.5f,.5f,1);
		s.setForeGroundColor(1,1,1,1);
		notifications = new ArrayList<ScreenNotification>();
		running = true;
		keyListener = new KeyInput();
		mouseListener = new MouseInput();
	}
	public void gameLoop()
	{
		double startTime = System.currentTimeMillis();
		double cumTime = startTime;
		while(running)
		{
			timePassed = System.currentTimeMillis() - cumTime;
			rate = timePassed/(16.3333d);
			cumTime += timePassed;
			update(timePassed);
			Graphics2D g = s.getGraphics();
			drawBackGround(g);
			draw(g);
			drawScreenNotifications();
			g.dispose();
			s.update();
			mouseListener.update();
			keyListener.update();
			
			try{
				Thread.sleep(17-(int)timePassed);
			}catch(Exception ex){}
			
		}
	}
	public static Graphics2D getGraphics()
	{
		return s.getGraphics();
	}
	private void drawBackGround(Graphics2D g)
	{
		g.setColor(s.getScreenWindow().getBackground());
		g.fillRect(0, 0, s.getWidth(), s.getHeight());
		g.setColor(s.getScreenWindow().getForeground());
	}
	private synchronized void drawScreenNotifications()
	{
		for(ScreenNotification notification:notifications)
		{
			notification.draw();
		}
	}
	//update animation
	public void update(double timePassed){
		
	}
	public void draw(Graphics2D g){
		if(KeyInput.keyPressed("Escape"))
			stop();
		if(KeyInput.keyPressed("Ctrl"))
			Core.s.changedScreenMode();
	}
}
