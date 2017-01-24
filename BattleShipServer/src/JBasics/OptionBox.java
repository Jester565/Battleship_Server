package JBasics;

import java.util.ArrayList;

import pack.Core;

public class OptionBox{
	private int x;
	private int y;
	private int w;
	private String selectedString;
	private ArrayList<String> strings;
	private int fontH;
	private double selectTimer = 0;
	private boolean open = false;
	public OptionBox(){
		
	}
	public OptionBox(int x, int y, int w, int fontH, ArrayList<String> strings){
		this.x = x;
		this.y = y;
		this.w = w;
		this.strings = strings;
		this.fontH = fontH;
		trimStrings();
		selectedString = strings.get(0);
	}
	private void trimStrings(){
		for(int i = 0; i < strings.size(); i++){
			String s = strings.get(i);
			int j = s.length();
			while(j > 0 && ShapeRenderer.getFontWidth(s.substring(0,j),fontH)*1.1d > w){
				j--;
				s = s.substring(0, j);
			}
			strings.set(i, s);
		}
	}
	public void draw(ShapeRenderer sr, String s, boolean filter){
		draw(sr,filter);
		sr.drawText(s, x, y + (int)(fontH * 1.5f), fontH, 0, 0, 0, 1);
	}
	public void draw(ShapeRenderer sr, boolean filter){
		selectTimer -= Core.rate;
		sr.drawRectangle(true, x, y, w, (int)(fontH *1.3));
		sr.drawRectangle(true, x + w, y, (int)(w/10d), (int)(fontH * 1.3d), .77f,.77f, .77f, 1);
		sr.drawText(selectedString, x, y + fontH/3, fontH, 0, 0, 0, 1);
		String arrow ="\\/";
		if(open)
			arrow = "/\\";
		if(Button.hitDrawnButton(sr, arrow, x + w, y, (int)(w/10d), (int)(fontH * 1.3d), .77f,.77f, .77f, 1) && selectTimer < 0){
			open = !open;
			selectTimer = 30;
		}
		if(open){
			int h = y;
			for(int i = 0; i < strings.size(); i ++){
				if(!strings.get(i).equals(selectedString)){
					h-=fontH*1.3;
					if(Button.hitDrawnButton(sr, "", x, h, w, (int)(fontH * 1.3f),.66f,.66f,.66f,1) && selectTimer < 0){
						selectedString = strings.get(i);
						selectTimer = 30;
					}
					sr.drawText(strings.get(i), x + 2, h + fontH/3, fontH,0,0,0,1);
				}
			}
		}
	}
	public String getSelectedString(){
		return selectedString;
	}
}