package JServer;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Arrays;

import JBasics.ActivityLog;
import JServer.ClientManager.Client;

public class PackageProcessing {

	private static ArrayList<Key> keys;
	public PackageProcessing(){
		keys = new ArrayList<Key>();
	}
	public static synchronized void processPackage(boolean safe, DatagramPacket pack, Client c){
		String locationKey = trimPackKey(pack);
		System.out.println(locationKey);
		double time = System.currentTimeMillis();
		if(locationKey.substring(0,1).compareTo("C") <= 0){
			if(c!=null){
				Server.getInternetDI().addPack(c, locationKey, pack,time,safe);
			}
			iterateKeyList(locationKey,pack,c,time);
		}else
			manageAllSend(safe,locationKey,pack,time,c);
	}
	public static void manageAllSend(boolean safe, String key, DatagramPacket pack,double time,Client cRecievedFrom){
		String firstKeyChar = key.substring(0,1);
		if(firstKeyChar.compareTo("I") <= 0){
			if(cRecievedFrom!=null){
				Server.getInternetDI().addPack(cRecievedFrom, key, pack,time,safe, null);
			}
			ByteToObject.addData(pack, ByteToObject.mergeData(key.getBytes(),ByteToObject.byteToByteList(new Integer(Server.getClientManager().getClient(pack.getAddress(), pack.getPort()).getID()).byteValue())), 0);
			Server.getClientManager().sendToAll(pack, safe, pack.getAddress(), pack.getPort(),time);
		}else if(firstKeyChar.compareTo("O") <= 0){
			Client c = Server.getClientManager().getClient(new Integer(pack.getData()[0]));
			ByteToObject.removeData(pack, 0, 1);
			if(cRecievedFrom!=null){
				Server.getInternetDI().addPack(cRecievedFrom, key, pack,time,safe,new ArrayList<Client>(Arrays.asList(c)));
			}
			ByteToObject.addData(pack, ByteToObject.mergeData(key.getBytes(),ByteToObject.byteToByteList(new Integer(Server.getClientManager().getClient(pack.getAddress(), pack.getPort()).getID()).byteValue())), 0);
			if(c!=null)
				c.send(pack, safe,time);
		}else if(firstKeyChar.compareTo("V") <= 0){
			int index = 0;
			ArrayList<Client> clients = new ArrayList<Client>();
			while(true){
				int id = new Integer(pack.getData()[index]);
				if(id > -1){
					Client c = Server.getClientManager().getClient(id);
					if(c!=null)
						clients.add(c);
				}else{
					break;
				}
				index++;
			}
			ByteToObject.removeData(pack, 0, index + 2);
			if(cRecievedFrom!=null){
				Server.getInternetDI().addPack(cRecievedFrom, key, pack,time,safe,clients);
			}
			ByteToObject.addData(pack, ByteToObject.mergeData(key.getBytes(),ByteToObject.byteToByteList(new Integer(Server.getClientManager().getClient(pack.getAddress(), pack.getPort()).getID()).byteValue())), 0);
			for(Client c: clients){
				c.send(pack, safe,time);
			}
		}else{
			try {
				throw new Exception("Key " + key + " was not found");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static void removeKey(Key key){
		for(int i = 0; i < keys.size(); i++){
			if(keys.get(i).equals(key)){
				keys.remove(i);
				return;
			}
		}
	}
	private static String trimPackKey(DatagramPacket pack){
		byte[] packData = pack.getData();
		byte[] keyData = new byte[Server.PACKET_INFO_LENGTH];
		byte[] trimData = new byte[pack.getData().length - Server.PACKET_INFO_LENGTH];
		for(int i = 0; i < packData.length; i++){
			if(i < Server.PACKET_INFO_LENGTH)
				keyData[i] = packData[i];
			else
				trimData[i - Server.PACKET_INFO_LENGTH] = packData[i];
		}
		pack.setData(trimData);
		return new String(keyData);
	}
	public static synchronized void addToKeyList(Key key){
		keys.add(key);
	}
	private static void iterateKeyList(String locKey, DatagramPacket pack,Client c,double time){
		for(Key key: keys){
			if(key.run(locKey, pack, c,time))
				return;
		}
		try {
			throw new Exception("Key " + locKey +" was not found.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
