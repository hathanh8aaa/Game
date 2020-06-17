package game.client.com;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.LinkedList;

import game.client.bean.Command;
import game.client.bean.Player;
import game.client.bean.gameSetUp;

public class Communication {
	public static String sentence;
	public static int server_port = Config.PORT;
	public static String server_ip = Config.LOCAL_IP;
	public static Socket clientSocket = null;
	public static BufferedReader inFromUser;
	public static DataOutputStream outToServer;
	public static BufferedReader inFromServer;
	private Player player;
	public static gameSetUp gamesS;
	public LinkedList<Command> queueCommands = new LinkedList<Command>();
	//public List<Player> lstPlayers = new LinkedList<Player>();
	
	public Communication(Player player, gameSetUp game) {
		this.player = player;
		this.gamesS = game;
	}
	
	public static void Receive() {
		Runnable run = new Runnable() {
			public void run() {
				try {
					while(true) {
						String message = inFromServer.readLine();
						gamesS.start();
						if(message.startsWith(Config.RENDERENEMY_CODE)) {
							gamesS.manager.PushToQueue(message);
						}
						else if(message.startsWith(Config.MOVE_CODE)) {
							message = message.substring(4);
							String[] query = message.split("\\|");
							for(Player fr : gamesS.manager.friends) {
								if(fr.getUserName().equals(query[0])) {
									if (query[1].equals("left")) {fr.TurnLeftByHost();}
									else if(query[1].equals("right")){fr.TurnRightByHost();}
									else if(query[1].equals("fire")){fr.FireByHost();}
									}else break;
								}
						}
						else if(message.startsWith(Config.STATUS_IN_GAME)) {
							message = message.substring(4);
							if(message.equals("gameover")) {
								gamesS.manager.setStart(false);
								gamesS.manager.setReadyToPlay(false);
								gamesS.manager.setGameStatus("Game Over!");
							}
						}
						else if(message.startsWith(Config.STARTGAME_CODE)) {		
							message = message.substring(4);
							if(message.equals("failed")) {
								gamesS.manager.setGameStatus("Wait for other players!");
							}
							else if(message.startsWith("waithost")) {
								String[] usernames = message.split("\\|");
								for(int i = 1; i<usernames.length;i++) {
									if(!usernames[i].equals(gamesS.manager.player.getUserName())) {
										gamesS.manager.addFriend(usernames[i]);
									}
								}
								
								gamesS.manager.setGameStatus("Wait host start game!");
							}
							else if(message.startsWith("ready")) {
								String[] usernames = message.split("\\|");
								for(int i = 1; i<usernames.length;i++) {
									if(!usernames[i].equals(gamesS.manager.player.getUserName())) {
										gamesS.manager.addFriend(usernames[i]);
									}
								}
								
								gamesS.manager.setGameStatus("Press enter to start!");
								gamesS.manager.setReadyToPlay(true);
								gamesS.manager.player.setHost(true);
							}
							else if(message.equals("ok")){
								//gamesS.manager.renderFriends();
								gamesS.manager.StartByHost();
							}
						}
						
						//System.out.println(message);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		};
		
		Thread receive = new Thread(run);
		receive.start();
	}
	
	public static void Send(String message) {
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
			bw.write(message);
	        bw.newLine();
	        bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Player getPlayer() {
		return player;
	}
	public void Init() {
		try {
			//gamesS.init();
			System.out.println("Connecting to TCP Server at: [" + server_ip + ":" + server_port + "]");
			clientSocket = new Socket(server_ip, server_port);
			
			System.out.println("Server connected. Local client port: " + clientSocket.getLocalPort());
			inFromUser = new BufferedReader(new InputStreamReader(System.in));
			outToServer = new DataOutputStream(clientSocket.getOutputStream());
			inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			outToServer.writeBytes("log "+ player.getUserName() + '\n');
			
			Receive();
			
		} catch (Exception e) {
			System.out.println("Cannot connect to TCP Server.\n Please check the server and run tcpclient again.");
			System.exit(0);
		}
	}
}
