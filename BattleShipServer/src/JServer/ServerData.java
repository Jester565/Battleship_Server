package JServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.util.ArrayList;

import JBasics.ActivityLog;
import JServer.ClientManager.Client;

public class ServerData {

	private ArrayList<DataBase> dataBases;
	protected File f;
	private Key keyA2;
	public ServerData(){
		keyA2 = new KeyA2();
		PackageProcessing.addToKeyList(keyA2);
		dataBases = new ArrayList<DataBase>();
		String modifiedPath = System.getProperty("user.dir");
		modifiedPath += "\\servData.sav";
		f = new File(modifiedPath);
		if(!f.exists()){
			try {
				FileOutputStream fos = new FileOutputStream(f);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				createObjects(oos);
				oos.close();
				fos.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		FileInputStream fis;
		try {
			fis = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(fis);
			loadObjects(ois);
			ActivityLog.addToLog("Loading Sucessful...");
			ois.close();
			fis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void dispose(){
		PackageProcessing.removeKey(keyA2);
	}
	protected void createObjects(ObjectOutputStream oos) throws IOException{
		oos.writeObject(new Boolean(false));
		oos.writeObject(new ArrayList<String>());
		oos.writeObject(new ArrayList<String>());
		ActivityLog.addToLog(new String("No save files could be found... creating with defaults"));
	}
	protected void loadObjects(ObjectInputStream ois) throws ClassNotFoundException,IOException{
		ActivityLog.addToLog("Objects loading...");
		Server.getInternetDI().createAllowManager(((Boolean)(ois.readObject())).booleanValue(), (ArrayList<String>)ois.readObject(), (ArrayList<String>)ois.readObject());
	}
	public void addDataBase(DataBase base){
		dataBases.add(base);
	}
	public void save(){
		ArrayList<Object> objs = new ArrayList<Object>();
		setSave(objs);
		saveObjects(objs);
	}
	public void setSave(ArrayList<Object> objs){
		ArrayList<Object> diObjs = Server.getInternetDI().getAllowManager().getSaveObjects();
		for(int i = 0; i < diObjs.size(); i++){
			objs.add(diObjs.get(i));
		}
	}
	private void saveObjects(ArrayList<Object> objs){
		try {
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			for(int i = 0; i < objs.size(); i++){
				oos.writeObject(objs.get(i));
			}
			oos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public class KeyA2 implements Key{
		String key = "A2";
		public boolean run(String key, DatagramPacket pack, Client c, double time){
			if(this.key.equals(key)){
				ArrayList<Object> objs = ByteToObject.bytesToObjects(pack);
				String type = (String)(objs.get(0));
				ActivityLog.addToLog(new String("The data base type " + type + " was requested"));
				for(DataBase db: dataBases){
					ArrayList<Object> sendObjs = db.getObjects(type,objs);
					if(objs != null){
						if(c!=null)
							c.send(ByteToObject.objectsToBytes("A2", "Basics", sendObjs), true, time);
						return true;
					}
				}
				try {
					ActivityLog.addToLog(new String("The data base type " + type + " was not found"));
					throw new Exception("The type " + type + " was not found in the data bases..");
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
			return false;
		}
		public String getDescription(){
			return new String(key + " Recieves packet and sends back all server data");
		}
	}
	public abstract class DataBase{
		protected ArrayList<Object> sendObjs;
		protected String type;
		public DataBase(String type){
			sendObjs = new ArrayList<Object>();
			this.type = type;
		}
		public ArrayList<Object> getObjects(String type, ArrayList<Object> objs){
			if(this.type.equals(type)){
				return sendObjs;
			}
			return null;
		}
	}
}
