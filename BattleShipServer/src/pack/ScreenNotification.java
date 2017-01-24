package pack;

import JBasics.Button;
import JBasics.ShapeRenderer;


public class ScreenNotification {

	String notify = null;
	boolean start = false;
	int y = 1080;
	double sitTimer = 0;
	ShapeRenderer sr;
	ScreenNotification()
	{
		sr = new ShapeRenderer();
	}
	public synchronized static void create(String s)
	{
		ScreenNotification notification = new ScreenNotification();
		notification.setNotification(s);
		Core.notifications.add(notification);
	}
	private synchronized void setNotification(String s)
	{
		notify = s;
		start = true;
	}
	public synchronized void draw()
	{
		if(Button.overButton(800, y, 400, 50))
		{
			if(sitTimer > 50)
				sitTimer = 50;
		}
		if(start && y > 1083 - 55)
		{
			y-=Core.rate*3;
		}
		else if (sitTimer < 100)
		{
			sitTimer+=Core.rate;
		}
		else if (y < 1083)
		{
			start = false;
			y+= Core.rate*3;
		}
		sr.drawRectangle(true, 800, y, 400, 50,1,0,0,1);
		sr.drawText(notify, 820, y+10,30,1,1,1,1);
	}
}
