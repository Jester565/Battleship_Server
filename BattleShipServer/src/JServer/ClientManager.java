package JServer;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;

import pack.Core;
import JBasics.ActivityLog;

public class ClientManager {

	public ArrayList<Client> clients;
	private Key keyA1;
	private int id = 0;
	private double maxPing = 10000;
	private double updatePing = 2000;
	public ClientManager(){
		clients = new ArrayList<Client>();
		keyA1 = new KeyA1();
		PackageProcessing.addToKeyList(keyA1);
		PackageProcessing.addToKeyList(new KeyA3());
	}
	public void setMaxPing(double ping){
		maxPing = ping;
	}
	public void dispose(){
		PackageProcessing.removeKey(keyA1);
	}
	public ArrayList<Client> getClients(){
		return clients;
	}
	public void kickName(String name, String message){
		for(int i = 0; i < clients.size(); i++){
			if(name.equals(clients.get(i).getName())){
				clients.get(i).send(ByteToObject.stringToBytes("A4", message), true, 0);
			}
		}
	}
	public void addClient(DatagramPacket pack){
		String name = getModifiedName(pack);
		if(Server.getInternetDI().allow(name)){
			if(getClient(pack.getAddress(), pack.getPort()) == null){
				Client c = new Client(id, name, pack.getAddress(), pack.getPort() + 1);
				clients.add(c);
				id++;
			}
		}else{
			Server.send(ByteToObject.stringToBytes("A4", "You are either not white listed or are banned from this server"), pack.getAddress(), pack.getPort(), 0);
		}
	}
	private String getModifiedName(DatagramPacket pack){
		String s = ByteToObject.bytesToString(pack);
		int nameNumber = 1;
		while(isAnExistingName(s)){
			String sub = s.substring(s.length()-1,s.length());
			if(sub.equals(")")){
				nameNumber++;
				s=s.substring(0,s.length()-3);
			}
			s += "(" + nameNumber + ")";
		}
		return s;
	}
	public void removeClient(Client c){
		for(int i = 0; i < clients.size(); i++){
			if(clients.get(i).equals(c)){
				Server.getInternetDI().removeClientBox(clients.get(i));
				sendToAll(ByteToObject.intToBytes("A5", clients.remove(i).getID()), true,0);
				return;
			}
		}
	}
	public void manage(){
		for(int i = 0; i < clients.size(); i++){
			if(clients.get(i).updatePing(updatePing)){
				removeClient(clients.get(i));
				i--;
			}
		}
	}
	public synchronized Client getClient(InetAddress address, int port){
		for(int i = 0; i < clients.size(); i++){
			if(clients.get(i).getAddress().equals(address) && clients.get(i).getPort() == port)
				return clients.get(i);
		}
		return null;
	}
	public Client getClient(int id){
		for(int i = 0; i < clients.size(); i++){
			if(clients.get(i).getID() == id)
				return clients.get(i);
		}
		try {
			throw new Exception("Client not found with id " + id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public void sendToAll(DatagramPacket pack, boolean safe, Client exception, double time){
		for(Client c: clients){
			if(!c.equals(exception) && allCheck(c,pack))
				c.send(pack, safe,time);
		}
	}
	public void sendToAll(DatagramPacket pack, boolean safe, double time){
		for(Client c: clients){
			c.send(pack, safe,time);
		}
	}
	public void sendToAll(byte[] data, boolean safe, double time){
		for(Client c: clients){
			c.send(data, safe,time);
		}
	}
	public void sendToAll(DatagramPacket pack, boolean safe, String exceptionName, double time){
		for(Client c: clients){
			if(!c.getName().equals(exceptionName) && allCheck(c,pack))
				c.send(pack, safe,time);
		}
	}
	public void sendToAll(DatagramPacket pack, boolean safe, InetAddress exceptionAddress, int port, double time){
		for(Client c: clients){
			if(!c.getAddress().equals(exceptionAddress) || c.getPort() != port)
				if(allCheck(c,pack))
					c.send(pack, safe,time);
		}
	}
	public void sendToAll(byte[] data, boolean safe, Client exception, double time){
		for(Client c: clients){
			if(!c.equals(exception) && allCheck(c,data))
				c.send(data, safe,time);
		}
	}
	public void sendToAll(byte[] data, boolean safe, String exceptionName, double time){
		for(Client c: clients){
			if(!c.getName().equals(exceptionName) && allCheck(c,data))
				c.send(data, safe,time);
		}
	}
	public void sendToAll(byte[] data, boolean safe, InetAddress exceptionAddress, int port, double time){
		for(Client c: clients){
			if(!c.getAddress().equals(exceptionAddress) || c.getPort() != port)
				if(allCheck(c,data))
					c.send(data, safe,time);
		}
	}
	public boolean isAnExistingName(String name){
		for(Client c: clients){
			if(c.getName().equals(name)){
				return true;
			}
		}
		return false;
	}
	protected boolean allCheck(Client c, byte[] data){
		return true;
	}
	private boolean allCheck(Client c, DatagramPacket pack){
		return allCheck(c, pack.getData());
	}
	public class KeyA3 implements Key{
		String key = "A3";
		public boolean run(String key, DatagramPacket pack, Client c, double time){
			if(this.key.equals(key)){
				c.resetCurPing(ByteToObject.bytesToDouble(pack));
				return true;
			}
			return false;
		}
		public String getDescription(){
			return new String(key + " : When recieved it will reset the clients curping");
		}
	}
	private ClientManager getClientManager(){
		return this;
	}
	public class Client{
		protected String name;
		protected int id;
		protected double curPing = 0;
		protected double ping = 0;
		protected InetAddress address;
		protected int port;
		protected TCPManager tcpManager;
		protected Thread tcpThread;
		private boolean pingSent = false;
		public boolean connected = false;
		public Client(int id, String name, InetAddress address, int port){
			this.id = id;
			this.name = name;
			this.address = address;
			this.port = port;
			ActivityLog.addToLog(new String("Client was added - Name: " + name + " id" + Integer.toString(id)));
			tcpManager = new TCPManager(name,id,address,port,this,getClientManager());
			tcpThread = new Thread(tcpManager);
			tcpThread.start();
			Server.getInternetDI().addClientBox(this);
		}
		public synchronized void setConnected(boolean mode){
			connected = mode;
		}
		public int getID(){
			return id;
		}
		public String getName(){
			return name;
		}
		public InetAddress getAddress(){
			return address;
		}
		public int getPort(){
			return port - 1;
		}
		public double getPing(){
			return ping;
		}
		public double getCurPing(){
			return curPing;
		}
		public void resetCurPing(double num){
			curPing = 0;
			pingSent = false;
			ping = System.currentTimeMillis() - num;
		}
		public boolean updatePing(double requiredToUpdate){
			if(connected){
				curPing+=Core.timePassed;
			}
			if(!pingSent && curPing > requiredToUpdate){
				pingSent = true;
				send(ByteToObject.doubleToBytes("A3", System.currentTimeMillis()),true,0);
			}
			if(curPing > maxPing){
				return true;
			}
			return false;
		}
		public void send(DatagramPacket pack, boolean safe,double time){
			if(safe){
				Server.getInternetDI().addPack(new String(pack.getData()).substring(0,2), pack, time, this,true);
				tcpManager.send(pack);
			}else
				Server.send(pack, address, getPort(),time);
		}
		public void send(byte[] data, boolean safe,double time){
			if(safe){
				Server.getInternetDI().addPack(new String(data).substring(0,2), new DatagramPacket(data,data.length,address,port), time, this,true);
				tcpManager.send(data);
			}else
				Server.send(data, address, getPort(),time);
		}
		public boolean equals(Object objs){
			Client c = (Client)objs;
			if(c.getAddress().equals(address) && c.getPort() == getPort())
				return true;
			return false;
		}
	}
	public class KeyA1 implements Key{
		String key = "A1";
		public boolean run(String key, DatagramPacket pack, Client c, double time){
			if(key.equals(this.key)){
				addClient(pack);
				return true;
			}
			return false;
		}
		public String getDescription(){
			return new String(key + " adds client to clients with the given name inside of the packet, the name may be modified");
		}
	}
}
