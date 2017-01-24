package JServer;

import java.net.DatagramPacket;

import JServer.ClientManager.Client;

public interface Key {

	public boolean run(String key, DatagramPacket pack, Client c, double time);
	public String getDescription();
}
