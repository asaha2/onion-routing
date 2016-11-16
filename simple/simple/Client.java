package simple;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import simple.Message;

/* usage: java Client <hostname> <port-number> */

public class Client extends Thread {
	public String hop1Hostname, hop2Hostname, serverHostname;
	public int hop1port, hop2port, serverPort = 80;
	boolean successfulConnection = false;
	private DataOutputStream os = null;
	private DataInputStream is = null;
	Proxy[] proxies = new Proxy[5];
	Thread t;

	public Client () {

	}

	public Client(Proxy[] proxies) {
		this.proxies = proxies;
	}

	public void run() {
		Client c = this;
		c.hop2Hostname = proxies[1].hostname;
		c.hop1Hostname = proxies[0].hostname;
		c.hop1port = proxies[0].recvPort;
		c.hop2port = proxies[1].recvPort;
		
		System.out.println(hop1Hostname + ":" + hop1port + " " + hop2Hostname + ":" + hop2port + " ");
		c.serverHostname = "www.google.com";
		c.serverPort = 80;

		c.startConnection();
	}

//	public void start() {
//		System.out.println("Starting Clinet");
//		if(t==null) {
//			t = new Thread("Client");
//			t.start();
//		}
//	}

	public boolean isConnSuccessful() {
		return successfulConnection;
	}




	private void initProxyConn(byte[] responseLine) throws IOException {
		// System.out.println("User input: " + userLine);
		Message create = new Message();
		create.type = Message.CellType.control;
		create.cmd = Message.Cmd.create;

		// for (byte mess : create.createMeassage()){
		os.write(create.createMessage());
		is.read(responseLine);
		if (responseLine != null) {
			Message created = Message.receiveMessage(responseLine);
			if (created.cmd != Message.Cmd.created)
				throw new IOException("Incorrect message");

			System.out.println("Response from server: " + created.cmd);
		}
	}

	public void initExtendedProxyConn(byte[] responseLine) throws IOException {
		Message extend = new Message();

		extend.type = Message.CellType.proxy;
		extend.cmd = Message.Cmd.extend;
		extend.data = (hop2Hostname + ":" + hop2port + "").getBytes();

		// for (byte mess : create.createMeassage()){
		os.write(extend.createMessage());
		is.read(responseLine);
		if (responseLine != null) {
			Message extented = Message.receiveMessage(responseLine);
			if (extented.cmd != Message.Cmd.extended)
				throw new IOException("Incorrect message");

			System.out.println("Response from server: " + extented.cmd);
		}
	}

	public void initDataTransfer(byte[] responseLine) throws IOException {
		Message request = new Message();

		request.type = Message.CellType.proxy;
		request.cmd = Message.Cmd.begin;
		request.data = (serverHostname + ":" + serverPort + "").getBytes();

		// for (byte mess : create.createMeassage()){
		os.write(request.createMessage());
		is.read(responseLine);
		if (responseLine != null) {
			Message response = Message.receiveMessage(responseLine);
			if (response.cmd != Message.Cmd.connected)
				throw new IOException("Incorrect message! Connected expected.");

			System.out.println("Response from server: " + response.cmd);
		}
	}


	public void generateMessage(byte[] responseLine) throws IOException {

		String text = "GET / HTTP/1.1\r\n"; // HTTP connection
		text += "Host: " + serverHostname + "\r\n\r\n";
		ArrayList<Message> messages = new ArrayList<>();

		for (Message request : Message.decompose(text)) {
			os.write(request.createMessage());
		}
		try {
			Scanner bleh= new Scanner( is);
			while ( bleh.hasNext()) {
				is.read(responseLine);
				//if (responseLine != null) {
					Message response = Message.receiveMessage(responseLine);
					// if (response.cmd!=Message.Cmd.connected) throw new
					// IOException("Incorrect message! Connected expected.");
					//
					// System.out.println("Response from server: " +
					// response.cmd);
					messages.add(response);
				//}

			}
		} catch (EOFException e) {
			
		}
		System.out.println(Message.compose((Message[]) messages.toArray()));
	}

	public void startConnection() {

		/* declaration of client socket and io streams */
		Socket clientSocket = null;

		BufferedReader isUser = null;
		// String responseLine, userLine;
		byte[] responseLine = new byte[512];

		/* open socket in hostname on port xxxx, initialize io streams */ 
		try{

			clientSocket = new Socket(hop1Hostname, hop1port);
			System.out.println("Created socket for " + hop1Hostname);
			os = new DataOutputStream(clientSocket.getOutputStream());
			is = new DataInputStream(clientSocket.getInputStream());
		} catch (UnknownHostException e) {
			System.err.println("Error: Unknown hostname.");
		} catch (IOException e) {
			System.err.println("Error: Unreachable host.");
		}


		/* continually transmit data via opened sockets */
		if (clientSocket != null && os != null && is != null) {
			// while(true){
			try {
				initProxyConn(responseLine);
				initExtendedProxyConn(responseLine);
				initDataTransfer(responseLine);
				generateMessage(responseLine);

			} catch (UnknownHostException e) {
				System.err.println("Trying to connect to unknown host: " + e);
			} catch (IOException e) {
				System.err.println("IOException:  " + e);
				// break;
			}
			// }
		}

		/* close io streams and opened sockets */
		try {
			os.close();
			is.close();
			clientSocket.close();
		} catch (UnknownHostException e) {
			System.err.println("Trying to connect to unknown host: " + e);
		} catch (IOException e) {
			System.err.println("IOException:  " + e);
		}

		successfulConnection = true;

	}

	public static void main(String[] args) {
		Client c = new Client();
		c.hop1Hostname= args[0];
		c.hop1port=Integer.parseInt(args[1]);
		c.hop2Hostname= args[2];
		c.hop2port=Integer.parseInt(args[3]);
		c.serverHostname="www.google.com";
		c.serverPort=80;
//=======
//		c.hop2Hostname = c.hop1Hostname = "127.0.0.1";
//		c.hop1port = 8080;
//		c.hop2port = 8082;
//		c.serverHostname = "www.google.com";
//		c.serverPort = 80;
//>>>>>>> 580d564... freezes

	c.startConnection();
		// Make header + message
		// get proxies
		// choose 2 proxies at random
		// sent create to 1st proxy
		// wait for the response
		// send extended create to 2nd proxy
		// wait for the extended response
		// Send the messane to proxy 1

	}

}
