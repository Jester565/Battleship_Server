package JServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import JServer.ClientManager.Client;

public class TCPManager implements Runnable{

	private Socket socket;
	private ServerSocket serverSocket;
	private DataInputStream in;
	private DataOutputStream out;
	private String name;
	private Client c;
	private ClientManager cm;
	private int id;
	public TCPManager(String name, int id, InetAddress hostAddress, int tcpSendPort, Client c, ClientManager cm){
		try {
			this.name = name;
			this.id = id;
			this.cm = cm;
			this.c = c;
			System.out.println("Bound Port: "+(tcpSendPort + 1));
			serverSocket = new ServerSocket(tcpSendPort + 1);
			serverSocket.setReuseAddress(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void run(){
		try {
			socket = serverSocket.accept();
			socket.setReuseAddress(true);
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			c.setConnected(true);
			send(ByteToObject.objectsToBytes("A1",new ArrayList<Object>(Arrays.asList(name, new Integer(id)))));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while(true){
			try {
				byte[] lengthData = new byte[Server.DIGIT_AMOUNT];
				in.read(lengthData,0,Server.DIGIT_AMOUNT);
				byte[] data = new byte[Integer.valueOf(new String(lengthData))];
				in.read(data,0,data.length);
				DatagramPacket packet = new DatagramPacket(data, data.length, socket.getInetAddress(), socket.getPort()-1);
				PackageProcessing.processPackage(true, packet,c);
			} catch (IOException e) {
				if(cm.getClient(c.getID()) == null){
					break;
				}
			}
		}
	}
	public void send(DatagramPacket packet){
		if(out!=null){
			byte[] data = packet.getData();
			data = modifyData(data);
			try {
				out.write(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void send(byte[] data){
		if(out!=null){
			data = modifyData(data);
			try {
				out.write(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private byte[] modifyData(byte[] bytes){
		byte[] data = new byte[Server.DIGIT_AMOUNT + bytes.length];
		String s = new String(Integer.toString(bytes.length));
		while(s.length() < Server.DIGIT_AMOUNT){
			s = "0" + s;
		}
		byte[] length = s.getBytes();
		for(int i = 0; i < data.length; i++){
			if(i < length.length)
				data[i] = length[i];
			else if(i >= length.length)
				data[i] = bytes[i - Server.DIGIT_AMOUNT];
		}
		return data;
	}
}
