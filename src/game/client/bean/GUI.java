package game.client.bean;

import game.client.com.Communication;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class GUI implements ActionListener {
	ArrayList<String> database = new ArrayList<String>();
	final String FILE_URL = "D:/Java Project/Game/Database.txt";
	JFrame frame;
	JButton btnSignIn;
	JButton btnSignUp;
	JPanel pnMain;
	JLabel lbUsername, lbPassword;
	GridBagLayout gridBagLayout;
	GridBagConstraints gbc;
	JTextField password, username;

	public GUI() {
		getDatabase();
		initContainer();
		initComponent();
		addComponent();
		addEvent();

	}

	public void getDatabase() {
		File file = new File(FILE_URL);
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader reader = new BufferedReader(inputStreamReader);

		String lineRead = "";
		String line = "";
		try {
			while ((lineRead = reader.readLine()) != null) {
				line += lineRead;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] accounts = line.split("___");
		for (String acc : accounts) {
			database.add(acc);
		}

	}

	public void updateDatabase(String acc) {
		File file = new File(FILE_URL);
		database.add(acc);
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(file, true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
		try {
			outputStreamWriter.write("___" + acc);
			outputStreamWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initContainer() {
		frame = new JFrame("Sky Force Game");
		frame.setSize(new Dimension(300, 300));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);

		pnMain = new JPanel();

		gridBagLayout = new GridBagLayout();
		gbc = new GridBagConstraints();

		pnMain.setLayout(gridBagLayout);
	}

	private void initComponent() {
		btnSignIn = new JButton("Sign in");
		btnSignUp = new JButton("Sign up");

		username = new JTextField();
		password = new JPasswordField();
		lbUsername = new JLabel("Username");
		lbPassword = new JLabel("Password");
	}

	private void addComponent() {
		frame.add(pnMain);

		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);

		gbc.gridx = 0;
		gbc.gridy = 0;
		pnMain.add(lbUsername, gbc);

		gbc.gridheight = 2;
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.BOTH;
		pnMain.add(username, gbc);

		gbc.gridheight = 1;
		gbc.gridx = 0;
		gbc.gridy = 3;
		pnMain.add(lbPassword, gbc);

		gbc.gridheight = 2;
		gbc.gridx = 0;
		gbc.gridy = 4;
		pnMain.add(password, gbc);

		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.gridx = 0;
		gbc.gridy = 6;
		pnMain.add(btnSignIn, gbc);

		gbc.gridx = 1;
		gbc.gridy = 6;
		pnMain.add(btnSignUp, gbc);

	}

	private void addEvent() {
		btnSignIn.setActionCommand("1");
		btnSignIn.addActionListener(this);
		btnSignUp.setActionCommand("2");
		btnSignUp.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		if ("1".equals(e.getActionCommand())) {
			String uname = username.getText();
			String pw = password.getText();
			if (uname != "") {
				boolean exist = false;
				String acc = uname + "|" + pw;
				for(String acc_in_db :database){
					if(acc.equals(acc_in_db)){
						exist = true;
						gameSetUp game = new gameSetUp("Sky Force Game!", 500, 600, uname);
						game.init();
						Communication comm = new Communication(game.manager.player,game);
						comm.Init();	
					}
				}
				if(!exist) JOptionPane.showMessageDialog(null, "Username or Password is incorrect !");
			}
		}else if ("2".equals(e.getActionCommand())) {
			
			JTextField username_su = new JTextField();
			JPasswordField password_su = new JPasswordField();
		    
	        Object[] message = {
	            "Username:", username_su,
	            "Password:", password_su
	        };
	        int option = JOptionPane.showOptionDialog(null,
	                message,
	                "Sign Up",
	                JOptionPane.YES_NO_CANCEL_OPTION,
	                JOptionPane.INFORMATION_MESSAGE,
	                null,
	                new String[]{"Sign Up", "Cancel"}, // this is the array
	                "default");
	        if (option == JOptionPane.YES_OPTION) {
	            // handle if click LOGIN
	            String uname = username_su.getText();
	            String pw = password_su.getText();
	            String new_account = uname + "|" + pw;
	            boolean exist = false;
	            for(String acc_in_db :database){
	            	String uname_in_db = acc_in_db.substring(0,acc_in_db.indexOf("|"));
	            	if(uname.equals(uname_in_db)){
	            		exist= true;
	            		break;
	            	}
				}
	            if(!exist){
	            	updateDatabase(new_account);
	            	JOptionPane.showMessageDialog(null, "Sucess !");
	            }else{
	            	JOptionPane.showMessageDialog(null, "This account has been existed!");
	            }
	        }
	                   
		}	

	}

	void showGUI() {
		frame.setVisible(true);
	}

}