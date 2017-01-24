package JBasics;

import java.util.ArrayList;

public class ActivityLog {

	static ShapeRenderer sr;
	static ArrayList<Message> messages;
	public static final int messageHeight = 20;
	public ActivityLog()
	{
		messages = new ArrayList<Message>();
		sr = new ShapeRenderer();
	}
	public static synchronized void addToLog(String s){
		messages.add(new Message(s));
	}
	public static synchronized void displayStrings(int x, int y, int w, int h, float fontSize){
		sr.drawRectangle(true, x, y, w, h,0,0,0,1);
		sr.drawRectangle(true, (int)(x + w/1.2f), y, (int)(w * .2f), h,.15f,.15f,.15f,1);
		int initialY = y;
		for(int i = messages.size() - 1; i >= 0; i--){
			if(y < initialY + h){
				y += messages.get(i).draw(sr, x + 5, y + (int)(fontSize * 1.2f) + 4, w, messageHeight, (int)fontSize,1,1,1,1,false);
			}else{
				messages.remove(i);
				i--;
			}
		}
	}
}
