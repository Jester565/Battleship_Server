package JServer;

import java.net.DatagramPacket;

import JServer.ClientManager.Client;

public class ServerSideKeys {

	public ServerSideKeys(){
		PackageProcessing.addToKeyList(new KeyA0());
	}
	public class KeyA0 implements Key{
		String key = "A0";
		public boolean run(String key, DatagramPacket pack, Client c, double time){
			if(this.key.equals(key)){
				byte[] send = ByteToObject.stringToBytes("A0", new String(Server.name + "~" +  new String(pack.getData())));
				DatagramPacket sendPack = new DatagramPacket(send, send.length);
				Server.send(sendPack,pack,time);
				return true;
			}
			return false;
		}
		public String getDescription(){
			return new String("Takes a pack with current time millis and sends back server name + \"FIN\" + time millis sent");
		}
	}
}
