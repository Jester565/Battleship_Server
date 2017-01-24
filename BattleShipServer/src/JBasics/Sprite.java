package JBasics;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import pack.Core;

public class Sprite {
	private float x = -700;
	private float y = -700;
	private float originX = 0;
	private float originY = 0;
	private double width = 0;
	private double height = 0;
	private int xFlip = 1;
	private int yFlip = 1;
	private double theta = 0;
	private double xScale = 1;
	private double yScale = 1;
	private BufferedImage img = null;
	private static final String constantFileName = "C:\\Users\\HP\\Desktop\\GameImages\\";
	int getWidth()
	{
		return img.getWidth();
	}
	int getHeight()
	{
		return img.getHeight();
	}
	void setToDefault()
	{
		x = -700;
		y = -700;
		originX = 0;
		originY = 0;
		width = 0;
		height = 0;
		xFlip = 1;
		yFlip = 1;
		theta = 0;
		xScale = 1;
		yScale = 1;
	}
	
	public void setImage(String s)
	{
		try {
		    img = ImageIO.read(new File(constantFileName+s));
		} catch (IOException e) {
			System.out.println("Could not find the image titled: " + s);
		}
	}
	
	public void setPosition(double x, double y)
	{
		this.x = (float)(x);
		this.y = (float)(y);
	}
	public void setPosition(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	public void setPosition(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public void setDegrees(double degrees)
	{
		this.theta = degrees * (Math.PI/180);
	}
	
	public void setRadians(double theta)
	{
		this.theta = theta;
	}
	
	public void setFlip(boolean x, boolean y)
	{
		if(x)
			xFlip = -1;
		else
			xFlip = 1;
		if(y)
			yFlip = -1;
		else
			yFlip = 1;
	}
	
	public void setOrigin(double originX, double originY)
	{
		this.originX = (float) originX;
		this.originY = (float) originY;
	}
	
	public void setOriginCenter()
	{
		originX = (float) (width/2);
		originY = (float) (height/2);
	}
	
	public void setScale(double xyScale)
	{
		double windowOff = 25;
		if(Core.s.isFullScreen())
		{
			windowOff = 0;
		}
		xScale = xyScale * (double)((double)(Core.s.getWidth()-windowOff)/1980d);
		yScale = xyScale * (double)((double)(Core.s.getHeight()-windowOff)/1080d);
	}
	
	public void draw()
	{
		Graphics2D g = Core.getGraphics();
		width = img.getWidth();
		height = img.getHeight();
		int x = (int) (this.x*xScale + (width*xScale) * (Math.abs(xFlip-1)/2));
		int y = (int) ((-this.y*yScale+Core.s.getHeight())-height*yScale + (height*yScale) * (Math.abs(yFlip-1)/2));
		g.scale(xScale,yScale);
		g.rotate(theta, x + originX*xFlip*xScale, y + originY*yFlip*yScale);
		g.drawImage(img, x, y, (int)(width * xFlip), (int)(height*yFlip),null);
		g.rotate(-theta, -(x+originX*xFlip*xScale), -(y+originY*yFlip*yScale));
		g.scale(1,1);
	}
}