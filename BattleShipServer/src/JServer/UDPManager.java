package JServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPManager implements Runnable{

	private DatagramSocket socket;
	private DatagramPacket packet;
	public UDPManager(int port)
	{
		try {
			socket = new DatagramSocket(port);
			socket.setReuseAddress(true);
			resetPacket();
			System.out.println("Local Address: " + InetAddress.getLocalHost());
			System.out.println("UDPAddress: " + socket.getLocalAddress() + " UDPPort: " + socket.getLocalPort());
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void run()
	{
		while(true){
			try {
				socket.receive(packet);
				byte[] data = packet.getData();
				byte[] sizeData = new byte[Server.DIGIT_AMOUNT];
				for(int i = 0; i < Server.DIGIT_AMOUNT; i++){
					sizeData[i] = data[i];
				}
				int size = Integer.valueOf(new String(sizeData));
				byte[] cutData = new byte[size];
				for(int i = 0; i < size;i++){
					cutData[i] = data[i + Server.DIGIT_AMOUNT];
				}
				packet.setData(cutData);
				packet.setLength(cutData.length);
				PackageProcessing.processPackage(false, packet,Server.getClientManager().getClient(packet.getAddress(),packet.getPort()));
				resetPacket();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private void resetPacket(){
		byte[] data = new byte[Server.RECIEVE_AMOUNT];
		packet = new DatagramPacket(data,data.length);
	}
	public void send(DatagramPacket sendPacket, DatagramPacket respondingPacket){
		sendPacket.setAddress(respondingPacket.getAddress());
		sendPacket.setPort(respondingPacket.getPort());
		byte[] bytes = sendPacket.getData();
		bytes = modifyData(bytes);
		sendPacket.setData(bytes);
		sendPacket.setLength(bytes.length);
		try {
			socket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void send(byte[] data, InetAddress address, int port){
		data = modifyData(data);
		DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, port);
		try {
			socket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void send(byte[] data, InetAddress address, int port, InetAddress sendFromAddress, int sentFromPort){
		data = modifyData(data);
		DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, port);
		try {
			socket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static byte[] modifyData(byte[] bytes){
		byte[] data = new byte[Server.DIGIT_AMOUNT + bytes.length];
		String s = new String(String.valueOf(bytes.length));
		while(s.length() < Server.DIGIT_AMOUNT){
			s = "0" + s;
		}
		byte[] length = s.getBytes();
		for(int i = 0; i < data.length; i++){
			if(i < Server.DIGIT_AMOUNT)
				data[i] = length[i];
			else if(i >= length.length)
				data[i] = bytes[i - Server.DIGIT_AMOUNT];
		}
		return data;
	}
}