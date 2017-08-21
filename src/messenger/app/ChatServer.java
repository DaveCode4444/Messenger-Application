package messenger.app;

/*
 * author - Devang Sawant
 * 
 */

import java.io.*;
import java.io.IOException;
import java.net.*;
import java.util.StringTokenizer;
import java.util.Vector;

public class ChatServer {

	private static Vector clientSockets;  //Vector is a growable array of objects
	private static Vector loginNames;	//these two vectors will have many clients and login names

	ChatServer() throws IOException{
		ServerSocket server = new ServerSocket(5217); //server socket: Socket is an endpoint of communication
		clientSockets = new Vector();
		loginNames = new Vector();

		while(true){
			Socket client = server.accept();
			AcceptClient acceptClient = new AcceptClient(client);
		}

	}

	class AcceptClient extends Thread {

		private Socket clientSocket;
		private DataInputStream din;
		private DataOutputStream dout;

		AcceptClient(Socket client) throws IOException{
			this.clientSocket = client;
			din = new DataInputStream(clientSocket.getInputStream());
			dout = new DataOutputStream(clientSocket.getOutputStream());

			String loginName = din.readUTF();
			loginNames.addElement(loginName);
			clientSockets.addElement(clientSocket);

			start();
		}
		
		//will print msgs to each client if a user logs in or logs out or writes a msg
		public void run(){
			while(true){
				try {
					String msgFromClient = din.readUTF();
					StringTokenizer st = new StringTokenizer(msgFromClient);
					String loginName = st.nextToken();
					String msgType = st.nextToken();
					String msg = "";
					int lo = -1;
					
					while(st.hasMoreTokens()){
						msg = msg+" "+st.nextToken();
					}
					if(msgType.equals("LOGIN")){
						for(int i=0;i<loginNames.size();i++){
							Socket pSocket = (Socket) clientSockets.elementAt(i);
							DataOutputStream pout = new DataOutputStream(pSocket.getOutputStream());
							pout.writeUTF(loginName+" has logged in.");
						}
					}else if(msgType.equals("LOGOUT")){
						for(int i=0;i<loginNames.size();i++){
							if(loginName.equals(loginNames.get(i))){
								lo = i;
							}
							Socket pSocket = (Socket) clientSockets.elementAt(i);
							DataOutputStream pout = new DataOutputStream(pSocket.getOutputStream());
							pout.writeUTF(loginName+" has logged out ");
						}
						if(lo>=0){
							loginNames.remove(lo);
							clientSockets.remove(lo);
						}
					}else{
						for(int i=0;i<loginNames.size();i++){
							Socket pSocket = (Socket) clientSockets.elementAt(i);
							DataOutputStream pout = new DataOutputStream(pSocket.getOutputStream());
							pout.writeUTF(loginName+": "+msg);
						}
					}
					
					if(msgType.equals("LOGOUT")){
						break;
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				System.out.println();

			}
		}




	}

	public static void main(String[] args) throws IOException {
		ChatServer chatServer = new ChatServer();
	}

}
