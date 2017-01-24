package JServer;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Arrays;

import pack.Core;
import JBasics.Button;
import JBasics.ButtonObj;
import JBasics.Coordinate;
import JBasics.FPSLogger;
import JBasics.OptionBox;
import JBasics.ShapeRenderer;
import JBasics.TextField;
import JServer.ClientManager.Client;

public class InternetDI {

	private ShapeRenderer sr;
	private ClientManager cm;
	private int angleDif = 0;
	private int x = 1000;
	private int y = 540;
	private double travelTime = 60;
	private static final int RADIUS = 400;
	public static final int X_LOCATION = 210;
	private ArrayList<ClientBox> clientBoxes;
	private ArrayList<PackBox> storedBoxes;
	private ArrayList<PackBox> activeBoxes;
	private ArrayList<KeyDisplay> trackKeys;
	private TextField enterKeys;
	private OptionBox selectFrames;
	private String keyText = "Remove Key";
	public static final int KEY_TRACK_HEIGHT = 150;
	private double deleteTrackKeyTimer = 0;
	private CornerDisplay cornerDisplay;
	private AllowManager allowManager;
	private double saveTimer = 0;
	private int saveMins = 1;
	private OptionBox setSaveMins;
	private OptionBox maxPing;
	private ButtonObj saveButton;
	public boolean drawOn = false;
	public double drawOnTimer = 0;
	public FPSLogger fpsLog;
	public InternetDI(ClientManager cm){
		enterKeys = new TextField(false,X_LOCATION,150,KEY_TRACK_HEIGHT - 50,40);
		sr = new ShapeRenderer();
		cornerDisplay = new CornerDisplay(X_LOCATION+200,0,1800,100);
		clientBoxes = new ArrayList<ClientBox>();
		storedBoxes = new ArrayList<PackBox>();
		activeBoxes = new ArrayList<PackBox>();
		trackKeys = new ArrayList<KeyDisplay>();
		maxPing = new OptionBox(500, 980,120,20, new ArrayList<String>(Arrays.asList("10","1","2","3","4","5","6","7","8","9","11","13","14","15","16","19","20")));
		setSaveMins = new OptionBox(1500, 980, 120,20, new ArrayList<String>(Arrays.asList("1", "2", "3", "4", "5","6","7","8","9","10","15","20")));
		saveButton = new ButtonObj();
		selectFrames = new OptionBox(900, 970, 200, 35, new ArrayList<String>(Arrays.asList("60", "5","10","15","20","25","30","35","40","45","50","55","70","80","90","100","120","140","160")));
		this.cm = cm;
		fpsLog = new FPSLogger();
	}
	public void switchMode(){
		drawOnTimer -= Core.rate;
		if(drawOnTimer < 0){
			drawOn = ! drawOn;
			drawOnTimer = 20;
		}
	}
	public void createAllowManager(boolean whiteListOn, ArrayList<String> whiteListNames, ArrayList<String> bannedNames){
		allowManager = new AllowManager(whiteListOn, whiteListNames,bannedNames);
	}
	private synchronized void drawTrackKeys(){
		deleteTrackKeyTimer -= Core.rate;
		if(drawOn){
			sr.drawRectangle(true,X_LOCATION, 0, 200, 1200,.3f,.3f,.3f,.3f);
			sr.drawRectangle(true,X_LOCATION, 0, 190, KEY_TRACK_HEIGHT + 85,.2f,.2f,.2f,.2f);
			sr.drawRectangle(true,X_LOCATION, 0, (int)(KeyDisplay.WIDTH * 1.8d), (int)(trackKeys.size() * (KeyDisplay.WIDTH * 1.2d) + KEY_TRACK_HEIGHT + 90),.2f,.2f,.2f,.2f);
			if(Button.hitDrawnButton(sr, "All Keys except", X_LOCATION, 80, 190, 50, .7f, .7f, .7f, .3f)){
				trackKeys.clear();
				keyText = "Remove Key";
			}
			if(Button.hitDrawnButton(sr, "No Keys except", X_LOCATION, 20, 190, 50, .7f, .7f, .7f, .3f)){
				trackKeys.clear();
				keyText = "Add Key";
			}
			enterKeys.draw(true, keyText);
			if(enterKeys.finalMessage != null){
				trackKeys.add(new KeyDisplay(enterKeys.finalMessage));
				enterKeys.reset();
			}
		}
		int height = KEY_TRACK_HEIGHT + 90;
		for(int i = 0; i < trackKeys.size(); i ++){
			trackKeys.get(i).draw(height);
			if(trackKeys.get(i).dead){
				trackKeys.remove(i);
				i--;
			}
			height += (int)(KeyDisplay.WIDTH * 1.2f);
		}
	}
	public synchronized void addPack(Client start, String locKey, DatagramPacket pack, double time, boolean tcp){
		String sendNames = "Server";
		if(keyText.equals("Add Key")){
			for(KeyDisplay kt:trackKeys){
				if(kt.getKey().equals(locKey)){
					activeBoxes.add(new PackBox(tcp, start,locKey,pack,time,sendNames));
					return;
				}
			}
		}else{
			for(KeyDisplay kt:trackKeys){
				if(kt.getKey().equals(locKey))
					return;
			}
			activeBoxes.add(new PackBox(tcp, start,locKey,pack,time,sendNames));
		}
	}
	public synchronized void addPack(Client start, String locKey, DatagramPacket pack, double time, boolean tcp, ArrayList<Client> clients){
		String sendNames = new String();
		if(clients == null){
			sendNames = "All";
		}else{
			for(Client c:clients){
				sendNames += c.getName()+" ";
			}
		}
		if(keyText.equals("Add Key")){
			for(KeyDisplay kt:trackKeys){
				if(kt.getKey().equals(locKey)){
					if(getClientBox(start) != null)
						activeBoxes.add(new PackBox(tcp, start,locKey,pack,time,sendNames));
					return;
				}
			}
		}else{
			for(KeyDisplay kt:trackKeys){
				if(kt.getKey().equals(locKey))
					return;
			}
			if(getClientBox(start) != null)
				activeBoxes.add(new PackBox(tcp, start,locKey,pack,time,sendNames));
		}
	}
	public boolean allow(String name){
		return allowManager.allow(name);
	}
	public synchronized void addPack(String locKey, DatagramPacket pack, double time,Client end, boolean tcp){
		if(keyText.equals("Add Key")){
			for(KeyDisplay kt:trackKeys){
				if(kt.getKey().equals(locKey)){
					if(time != 0)
						storedBoxes.add(new PackBox(tcp, locKey,pack,time,end));
					else
						activeBoxes.add(new PackBox(tcp, locKey,pack,time,end));
					return;
				}
			}
		}else{
			for(KeyDisplay kt:trackKeys){
				if(kt.getKey().equals(locKey))
					return;
			}
			if(time != 0)
				storedBoxes.add(new PackBox(tcp, locKey,pack,time,end));
			else
				activeBoxes.add(new PackBox(tcp, locKey,pack,time,end));
		}
	}
	public void removeClientBox(Client c){
		for(int i = 0; i < clientBoxes.size(); i++){
			if(clientBoxes.get(i).c.equals(c)){
				clientBoxes.remove(i);
				return;
			}
		}
	}
	private void manageAngles(){
		int targetAngleDif = 360/cm.clients.size();
		if(angleDif > targetAngleDif){
			angleDif--;
		}else if (angleDif < targetAngleDif){
			angleDif++;
		}
	}
	public synchronized void addClientBox(Client c){
		clientBoxes.add(new ClientBox(c));
	}
	public synchronized ClientBox getClientBox(Client c){
		for(ClientBox cb: clientBoxes){
			if(cb.c.equals(c)){
				return cb;
			}
		}
		return null;
	}
	public void checkID(PackBox pb,float r, float g, float b,String sendNames){
		for(int i = 0; i < storedBoxes.size(); i++){
			if(storedBoxes.get(i) != null && storedBoxes.get(i).equals(pb)){
				storedBoxes.get(i).setRGB(r, g, b);
				storedBoxes.get(i).setGoingNames(sendNames);
				activeBoxes.add(storedBoxes.remove(i));
				i--;
			}
		}
	}
	public void draw(){
		travelTime = Double.valueOf(selectFrames.getSelectedString());
		drawPackBoxes();
		if(drawOn && cm.clients.size() > 0){
			drawBox();
		}
		drawTrackKeys();
		if(drawOn){
			sr.drawCircle(true, x, y, 100,0,1,0,1);
			sr.drawText("Server", 932, y-15,50,0,0,0,1);
			fpsLog.drawFPS(sr,1800, 900,20);
			selectFrames.draw(sr, "Frame/Pack", false);
			allowManager.draw(1800, 100, 200, 985);
			cornerDisplay.draw();
		}
		saveTimer += Core.timePassed;
		if(drawOn){
			setSaveMins.draw(sr, "Set save mins", false);
			maxPing.draw(sr, "Max Ping", false);
		}
		cm.setMaxPing(Double.valueOf(maxPing.getSelectedString()) * 1000);
		saveMins = Integer.valueOf(setSaveMins.getSelectedString());
		if(saveTimer > saveMins * 1000 * 60 || drawOn && saveButton.clickButton(sr, "Save", 1650, 970, 100, 50, .3f, .3f, .3f, .3f)){
			callSave();
		}
	}
	private void callSave(){
		Server.getServerData().save();
		saveTimer = 0;
	}
	private void drawBox(){
		manageAngles();
		int angle = 90;
		for(ClientBox cb: clientBoxes){
			cb.draw(angle);
			angle+=angleDif;
		}
	}
	private void drawPackBoxes(){
		for(int i = 0; i < activeBoxes.size(); i++){
			activeBoxes.get(i).draw();
			if(activeBoxes.get(i).dead){
				activeBoxes.remove(i);
				i--;
			}
		}
	}
	public class KeyDisplay{
		private String key = "UN";
		public static final int WIDTH = 30;
		public boolean dead = false;
		public KeyDisplay(String key){
			this.key = key;
		}
		public void draw(int y){
			if(drawOn && Button.hitDrawnButton(sr, key, X_LOCATION + 5, y, WIDTH, WIDTH, .8f, .32f, .6f, 1) && deleteTrackKeyTimer < 0){
				dead = true;
				deleteTrackKeyTimer = 30;
			}
		}
		public String getKey(){
			return key;
		}
	}
	public AllowManager getAllowManager(){
		return allowManager;
	}
	public class AllowManager{
		private boolean whiteListOn = false;
		private ArrayList<String> whiteListNames;
		private ArrayList<String> bannedNames;
		private ButtonObj buttonObj1;
		private double removeTimer = 0;
		private int off = 0;
		private TextField typedNames;
		private ButtonObj upArrow;
		private ButtonObj downArrow;
		public AllowManager(boolean whiteListOn, ArrayList<String> whiteListNames, ArrayList<String> bannedNames){
			this.whiteListOn = whiteListOn;
			this.whiteListNames = whiteListNames;
			this.bannedNames = bannedNames;
			buttonObj1 = new ButtonObj(whiteListOn);
			typedNames = new TextField(true,1,1,1,1);
			upArrow = new ButtonObj();
			downArrow = new ButtonObj();
		}
		public ArrayList<Object> getSaveObjects(){
			ArrayList<Object> objs = new ArrayList<Object>();
			objs.add(new Boolean(whiteListOn));
			objs.add(whiteListNames);
			objs.add(bannedNames);
			return objs;
		}
		public void draw(int x, int y, int w, int h){
			sr.drawRectangle(true, x, y, w, h,.3f,.3f,.3f,.3f);
			removeTimer -= Core.rate;
			whiteListOn = (buttonObj1.hitButtonTimed(sr, "White Listing", x + w/5, y + (19*h)/20, w - w/2.5, h/20, .7f, .7f, .7f, 1));
			boolean upArrowOn = false;
			if(whiteListOn){
				for(int i = 0; i < whiteListNames.size();i++){
					if((i*h)/20 + y + h/6 + off < y + (16*h)/20){
						if((i*h)/20 + y + h/6 + off >= y + h/6 && Button.hitDrawnButton(sr, whiteListNames.get(i), x, (i*h)/20 + y + h/6 + off, w, h/20, .5f, .5f, .5f, .3f) && removeTimer < 0){
							whiteListNames.remove(i);
							removeTimer = 20;
							i--;
						}
					}else{
						upArrowOn = true;
					}
				}
				typedNames.draw(true, "Name:", x, y, w, h/20, 1, 1, 1, 1);
				if(typedNames.finalMessage != null){
					whiteListNames.add(typedNames.finalMessage);
					typedNames.reset();
				}
			}else{
				for(int i = 0; i < bannedNames.size();i++){
					if((i*h)/20 + y + h/6 + off< y + (16*h)/20){
						if((i*h)/20 + y + h/6 + off >= y + h/6&&Button.hitDrawnButton(sr, bannedNames.get(i), x, (i*h)/20 + y + h/6 + off, w, h/20, .5f, .5f, .5f, .3f) && removeTimer < 0){
							bannedNames.remove(i);
							removeTimer = 20;
							i--;
						}
					}else{
						upArrowOn = true;
					}
				}
				typedNames.draw(true, "Name:", x, y, w, h/20, 1, 1, 1, 1);
				if(typedNames.finalMessage != null){
					bannedNames.add(typedNames.finalMessage);
					typedNames.reset();
				}
			}
			if(upArrowOn && upArrow.clickButton(sr, "/\\", x + w/4, y + (17*h)/20, w/2, h/20, .8f, .8f, .8f, 1)){
				off-=80;
			}
			if(off < 0 && downArrow.clickButton(sr, "\\/", x + w/4, y + h/10, w/2, h/20, .8f, .8f, .8f, 1)){
				off+=80;
			}
		}
		public boolean allow(String name){
			if(inBan(name))
				return false;
			if(whiteListOn){
				for(String s: whiteListNames){
					if(name.equals(s)){
						return true;
					}
				}
			}else{
				return true;
			}
			return false;
		}
		private boolean inBan(String name){
			for(String s: bannedNames){
				if(name.equals(s)){
					return true;
				}
			}
			return false; 
		}
	}
	public class CornerDisplay{
		private int x;
		private int y;
		private int w;
		private int h;
		private String type = "----";
		private ArrayList<String> displayStrings;
		private ClientBox selectedClientBox;
		public CornerDisplay(int x, int y, int w, int h){
			this.x= x;
			this.y = y;
			this.w = w;
			this.h = h;
			displayStrings = new ArrayList<String>();
		}
		public void setInfo(PackBox pb){
			displayStrings.clear();
			type = "Packet";
			selectedClientBox = null;
			displayStrings.add(new String("Key: " + pb.key));
			displayStrings.add(new String("Sent by: " + pb.getSentClientBoxName()));
			displayStrings.add(new String("Going to: " + pb.getRecieveClientBoxName()));
			displayStrings.add(new String("Pack also to: " + pb.getGoingNames()));
			displayStrings.add(new String("In String form: ") + new String(pb.pack.getData()));
		}
		public void setInfo(ClientBox cb){
			displayStrings.clear();
			type = "Client";
			displayStrings.add(new String("Name: " + cb.c.getName()));
			displayStrings.add(new String("ID: " + cb.c.getID()+ " Address: " + cb.c.getAddress() + " Port: " + cb.c.getPort()));
			displayStrings.add(new String("Ping: " + cb.c.getPing()));
			displayStrings.add(new String("CurPing: " + cb.c.getCurPing()));
			selectedClientBox = cb;
		}
		public void draw(){
			sr.drawRectangle(true, x, y + h, ShapeRenderer.getFontWidth(type, 20) + 6, 23,.1f,.1f,.1f,.4f);
			sr.drawText(type, x, y+h,20,.7f,.7f,.7f,1);
			sr.drawRectangle(true, x, y, w, h,.2f,.2f,.2f,.2f);
			int pos = y + h;
			for(int i = 0; i < displayStrings.size(); i++){
				pos -= h/(displayStrings.size()+1);
				sr.drawText(displayStrings.get(i), x + 10, pos, h/(displayStrings.size()+1), 1, 1, 1, 1);
			}
			if(selectedClientBox != null){
				updateClientInfo(selectedClientBox);
			}
		}
		private void updateClientInfo(ClientBox cb){
			displayStrings.set(2,new String("Ping: " + cb.c.getPing()));
			displayStrings.set(3,new String("CurPing: " + cb.c.getCurPing()));
			if(Button.hitDrawnButton(sr, "Kick", x + (6*w)/10, y + h/4, w/30, h/2, 1, .3f, .3f, 1)){
				Server.getClientManager().kickName(cb.c.getName(), "Kicked by server");
			}
		}
	}
	public class ClientBox{
		private static final int width = 100;
		private static final int height = 30;
		private int cX;
		private int cY;
		private double pheta;
		private Client c;
		private int l1x1;
		private int l1y1;
		private int l1x2;
		private int l1y2;
		private int l2x1;
		private int l2y1;
		private int l2x2;
		private int l2y2;
		private float r = (float)(Math.random());
		private float b = (float)(Math.random());
		private float g = (float)(Math.random() * .8f);
		public ClientBox(Client c){
			this.c = c;
		}
		public float getR(){
			return r;
		}
		public float getB(){
			return b;
		}
		public float getG(){
			return g;
		}
		public Coordinate getPos(double time, boolean back){
			double pX;
			double pY;
			if(!back){
				pX = l1x1 + (int)(Math.cos(pheta) * RADIUS * ((double)(travelTime - time)/(double)travelTime));
				pY = l1y1 + (int)(Math.sin(pheta) * RADIUS * ((double)(travelTime - time)/(double)travelTime));
			}else{
				pX = l2x1 + (int)(Math.cos(pheta) * RADIUS * ((double)(time)/(double)travelTime));
				pY = l2y1 + (int)(Math.sin(pheta) * RADIUS * ((double)(time)/(double)travelTime));
			}
			return new Coordinate(pX, pY);
		}
		public boolean equals(float r, float g, float b){
			if(this.r == r && this.g == g && this.b == b)
				return true;
			return false;
		}
		public void draw(int angle){
			pheta = angle*(Math.PI/180d);
			cX = (int)(Math.cos(pheta) * RADIUS) + x;
			cY = (int)(Math.sin(pheta) * RADIUS) + y;
			l1x1 = (int)(Math.sin(pheta) * -height/2) + x;
			l1y1 = (int)(Math.cos(pheta) * -height/2) + y;
			l1x2 = (int)(cX + l1x1 - x);
			l1y2 = (int)(cY + l1y1 - y);
			l2x1 = (int)(Math.sin(pheta) * height/2) + x;
			l2y1 = (int)(Math.cos(pheta) * height/2) + y;
			l2x2 = (int)(cX + l2x1 - x);
			l2y2 = (int)(cY + l2y1 - y);
			sr.drawLine(l1x1, l1y1, l1x2, l1y2);
			sr.drawLine(l2x1, l2y1, l2x2, l2y2);
			sr.drawRectangle(true, cX - width/2, cY - height/2, width, height,r,g,b,1);
			if(Button.hitDrawnButton(sr, c.getName(), cX - width/2, cY - height/2, width, height,r,g,b,1)){
				cornerDisplay.setInfo(this);
			}
		}
	}
	public class PackBox{
		private String key;
		private DatagramPacket pack;
		private Client c;
		private ClientBox cb;
		private double timeSecs = 0;
		private double timeID;
		public boolean dead = false;
		public static final int PACK_RADIUS = 20;
		private boolean back = false;
		private boolean tcp = false;
		private float r = 0;
		private float b = 0;
		private float g = 1f;
		private float oR = 0;
		private float oG = 0;
		private float oB = 0;
		private String goingNames;
		public PackBox(boolean tcp, Client startClient, String locKey, DatagramPacket pack,double timeID,String goingNames){
			this.tcp = tcp;
			System.out.println(locKey);
			key = locKey;
			c = startClient;
			this.pack = pack;
			this.timeID = timeID;
			cb = getClientBox(startClient);
			setRGB(cb.getR(),cb.getG(),cb.getB());
			setRGB(1,1,1);
			this.goingNames = goingNames;
		}
		public String getSentClientBoxName(){
			if(back){
				for(ClientBox cb:clientBoxes){
					if(cb.equals(r,g,b))
						return cb.c.getName();
				}
			}else{
				return cb.c.getName();
			}
			return "unKnown";
		}
		public void setGoingNames(String s){
			goingNames = s;
		}
		public String getGoingNames(){
			return goingNames;
		}
		public String getRecieveClientBoxName(){
			if(back){
				return cb.c.getName();
			}else{
				return "Server";
			}
		}
		private void setRGB(float r, float g,float b){
			this.r = r;
			this.b= b;
			this.g = g;
		}
		public boolean equals(Object obj){
			if(((PackBox)obj).timeID == timeID)
				return true;
			return false;
		}
		public PackBox(boolean tcp, String locKey, DatagramPacket pack, double timeID, Client endClient){
			this.tcp = tcp;
			key = locKey;
			c = endClient;
			this.pack = pack;
			this.timeID = timeID;
			cb = getClientBox(endClient);
			back = true;
		}
		public void draw(){
			timeSecs += Core.rate;
			if(drawOn){
				if(cb != null){
					Coordinate c = cb.getPos(timeSecs,back);
					if(tcp)
						sr.drawCircle(true, (int)c.getX(), (int)c.getY(), PACK_RADIUS, r,g, b,1);
					else
						sr.drawRectangle(true, (int)c.getX() - PACK_RADIUS, (int)c.getY() - PACK_RADIUS, PACK_RADIUS*2, PACK_RADIUS * 2, r,g, b,1);
					sr.drawText(key, (int)(c.getX() - PACK_RADIUS/1.5d), (int)(c.getY() - PACK_RADIUS/1.5d),(int)(PACK_RADIUS),0,0,0,1);
					if(Button.hitButton(c.getX() - PACK_RADIUS, c.getY()-PACK_RADIUS, PACK_RADIUS*2, PACK_RADIUS*2)){
						cornerDisplay.setInfo(this);
					}
				}
			}
			if(timeSecs > travelTime){
				checkID(this,r, g, b, goingNames);
				dead = true;
			}
		}
	}
}
