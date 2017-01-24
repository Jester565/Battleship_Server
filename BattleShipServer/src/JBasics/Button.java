package JBasics;

import pack.MouseInput;

public abstract class Button {
	
	public static boolean overButton(double x, double y, double w, double h)
	{
		if(MouseInput.getX()>x && MouseInput.getX()<x+w && MouseInput.getY()>y && MouseInput.getY()<y+h)
		{
			return true;
		}
		return false;
	}
	public static boolean hitButton(double x, double y, double w, double h)
	{
		if(MouseInput.left() && MouseInput.getX()>x && MouseInput.getX()<x+w && MouseInput.getY()>y && MouseInput.getY()<y+h){
			return true;
		}
		return false;
	}
	public static boolean hitDrawnButton(ShapeRenderer sr, String s,double x, double y, double w, double h,float r, float g, float b, float a)
	{
		
		if(overButton(x,y,w,h)){
			sr.drawRectangle(true, (int)x, (int)y, (int)w, (int)h, r/1.5f,  g/1.5f,  b/1.5f, a);
			sr.drawCenteredText(s, (int)(x + w/2), (int)(y + h/9d), (float)(w/s.length()),0,0,0,1);
			if(MouseInput.left()){
				return true;
			}
		}
		else
		{
			sr.drawRectangle(true, (int)x, (int)y, (int)w, (int)h, r, g, b, a);
			sr.drawCenteredText(s, (int)(x + w/2), (int)(y + h/9d), (float)(w/s.length()),0,0,0,1);
		}
		return false;
	}
}
