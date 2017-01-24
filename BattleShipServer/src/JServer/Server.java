package JServer;

import java.awt.Graphics2D;
import java.net.DatagramPacket;
import java.net.InetAddress;

import JBasics.ActivityLog;
import JBasics.Button;
import JBasics.ShapeRenderer;
import JBasics.TextField;
import JServer.ClientManager.Client;
import pack.Core;
import pack.KeyInput;

public abstract class Server extends Core{

	public static final int DIGIT_AMOUNT  = 6;
	public static final int PACKET_INFO_LENGTH = 2;
	public static final int RECIEVE_AMOUNT = 16384;
	public static final int UDP_RECIEVE_PORT = 2650;
	public Thread udpThread;
	protected static InternetDI idi;
	protected static ServerData servData;
	protected static UDPManager udpManager;
	protected static ClientManager clientManager;
	public static String name;
	protected TextField serverName;
	protected ActivityLog log;
	protected ShapeRenderer sr;
	protected boolean serverRunning = false;
	public void init(){
		super.init();
		new PackageProcessing();
		new ServerSideKeys();
		log = new ActivityLog();
		clientManager = new ClientManager();
		sr = new ShapeRenderer();
		serverName = new TextField(true, 500, 400, 1000, 50);
		idi = new InternetDI(clientManager);
		servData = new ServerData();
	}
	public void draw(Graphics2D g){
		super.draw(g);
		if(!serverRunning){
			drawSetup();
		}else{
			drawRun();
		}
		idi.draw();
		if(KeyInput.keyPressed("F1"))
			idi.switchMode();
	}
	public static ServerData getServerData(){
		return servData;
	}
	public static InternetDI getInternetDI(){
		return idi;
	}
	public static ClientManager getClientManager(){
		return clientManager;
	}
	private void startUDP(){
		udpThread = new Thread(udpManager);
		udpThread.start();
	}
	protected void drawSetup(){
		drawNameEnter();
		drawExtraServerInfo();
		if(finishButton()){
			serverRunning = true;
			startServer();
		}
	}
	protected void drawRun(){
		ActivityLog.displayStrings(0, 0, 210, 1200, 20);
		clientManager.manage();
	}
	public static void send(DatagramPacket sendPack, DatagramPacket respondPack,double time){
		Client c = clientManager.getClient(respondPack.getAddress(),respondPack.getPort());
		if(c!=null)
			Server.getInternetDI().addPack(new String(sendPack.getData()).substring(0,2), sendPack, time, c, false);
		udpManager.send(sendPack, respondPack);
	}
	public static void send(DatagramPacket pack, InetAddress sendAddress, int sendPort,double time){
		Client c = clientManager.getClient(sendAddress,sendPort);
		if(c!=null)
			Server.getInternetDI().addPack(new String(pack.getData()).substring(0,2), pack, time, c, false);
		udpManager.send(pack.getData(), sendAddress, sendPort);
	}
	public static void send(byte[] data, InetAddress sendAddress, int sendPort,double time){
		Client c = clientManager.getClient(sendAddress,sendPort);
		if(c!=null)
			Server.getInternetDI().addPack(new String(data).substring(0,2), new DatagramPacket(data,data.length,sendAddress,sendPort), time, c, false);
		udpManager.send(data, sendAddress, sendPort);
	}
	protected void drawRunning(){
		
	}
	protected void drawLog(){
		
	}
	protected void startServer(){
		serverName.setFinalMessage();
		name = serverName.finalMessage;
		udpManager = new UDPManager(UDP_RECIEVE_PORT);
		startUDP();
	}
	protected void drawNameEnter(){
		serverName.draw(true, "Server Name:");
	}
	protected boolean finishButton(){
		if(Button.hitDrawnButton(sr, "Create Server", 800, 200, 400, 70, 0, 1, 0, 1))
			return true;
		return false;
	}
	protected void drawExtraServerInfo(){
		
	}
}
