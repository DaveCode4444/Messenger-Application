package messenger.app;

/*
 * author - Devang Sawant
 * 
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.omg.CORBA.portable.UnknownException;

import javafx.scene.control.ScrollPane;

public class ChatClient extends JFrame implements Runnable { //JFrame is a swing object

	private Socket socket;
	private JTextArea ta;
	private Thread thread;
	private DataInputStream din;
	private DataOutputStream dout;
	private String loginName;
	private JButton send;
	private JButton logout;
	private JTextField tf;


	ChatClient(String login) throws UnknownHostException, IOException{
		super(login); //super constructor is to set the JFrame

		this.loginName = login;
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				try {
					dout.writeUTF(loginName+ " "+ "LOGOUT");
					System.exit(1);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		this.ta = new JTextArea(18,50);
		this.tf = new JTextField(50);
		this.send = new JButton("Send");
		this.logout = new JButton("Logout");

		tf.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e) {

				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					try {
						if(tf.getText().length()>0){
							dout.writeUTF(loginName+" "+ "DATA "+ tf.getText().toString());
							tf.setText("");
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
			}
		});

		send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if(tf.getText().length()>0){
						dout.writeUTF(loginName+" "+ "DATA "+ tf.getText().toString());
						tf.setText("");
					}} catch (IOException e1) {
						e1.printStackTrace();
					}

			}
		});

		logout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					dout.writeUTF(loginName+" "+ "LOGOUT");
					System.exit(1);
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}
		});
		this.socket = new Socket("localhost",5217);

		din = new DataInputStream(socket.getInputStream());
		dout = new DataOutputStream(socket.getOutputStream());

		dout.writeUTF(loginName);
		dout.writeUTF(loginName+" "+ "LOGIN");
		thread = new Thread(this);
		thread.start();
		setup();
	}


	private void setup() {  //setting up Jframe 
		setSize(600,400);

		JPanel panel = new JPanel();
		panel.add(new JScrollPane(ta));
		panel.add(tf);
		panel.add(send);
		panel.add(logout);
		add(panel);

		setVisible(true);


	}


	@Override
	public void run() {

		while(true){
			try {
				ta.append("\n"+din.readUTF());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}





}
