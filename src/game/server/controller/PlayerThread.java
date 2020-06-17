package game.server.controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import game.server.bean.Command;
import game.server.bean.Player;
import game.server.common.Config;

public class PlayerThread extends Thread {
	public Socket connectionSocket;
	public ServiceHandler serviceHandler=null;
	public Player player=null;
	BufferedReader infromClient;
	DataOutputStream outToCLient;
	
	public PlayerThread(Socket socket, ServiceHandler serviceHandler, Player player) {
		this.connectionSocket = socket;
		this.serviceHandler = serviceHandler;
		this.player = player;
	}
	
	@Override
	public void run() {
		try {
			infromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			outToCLient = new DataOutputStream(connectionSocket.getOutputStream());
			String username = infromClient.readLine();
			if(username.startsWith(Config.LOGIN_CODE)) {
				username = username.substring(4);
				player.setUsername(username);
			}
		
			while(true) {
				String message = infromClient.readLine();
				if(serviceHandler.isRunning()) {
					Command cmd = new Command(player, message);
					serviceHandler.PushToQueue(cmd);
				}
				else {
					outToCLient.writeBytes(Config.STARTGAME_CODE+" ready\n");
				}
			}
		} catch (Exception e) {
			
		}
	}
}
