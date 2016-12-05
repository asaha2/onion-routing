package simple;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;


import simple.Message;

/* usage: java Client <hostname> <port-number> */

public class Client extends Thread {
	public String hop1Hostname,hop2Hostname ,hop3Hostname, serverHostname;
	public int hop1port=8080, hop2port=8080, hop3port=8080, serverPort = 80;
	boolean successfulConnection = false;
	private DataOutputStream os = null;
	private DataInputStream is = null;
	Proxy[] proxies = new Proxy[5];
	Thread t;
	int hop1Key=0, hop2Key=0, hop3Key=0;

	public Client() {

	}

	public Client(Proxy[] proxies) {
		this.proxies = proxies;
	}

	public void run() {//test
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
	
	public boolean isConnSuccessful() {
		return successfulConnection;
	}

	private void initProxyConn(byte[] responseLine) throws IOException {
		// System.out.println("User input: " + userLine);
		Message create = new Message();
		create.type = Message.CellType.CONTROL;
		create.cmd = Message.Cmd.CREATE;

		// for (byte mess : create.createMeassage()){
		os.write(create.createMessage());
		is.read(responseLine);
		if (responseLine != null) {
			Message created = Message.receiveMessage(responseLine);
			if (created.cmd != Message.Cmd.CREATED)
				throw new IOException("Incorrect message");
			hop1Key = responseLine[3];
			System.out.println("Successfully CREATED connection with "+hop1Hostname+":"+hop1port+" Using Cipher Key "+hop1Key);
		}
	}

	public int initExtendedProxyConn(byte[] responseLine, String host, int port, int ... keys) throws IOException {
		Message extend = new Message();

		extend.type = Message.CellType.PROXY;
		extend.cmd = Message.Cmd.EXTEND;
		extend.data = (host + ":" + port + "").getBytes();

		// for (byte mess : create.createMeassage()){
		os.write(extend.createMessage(keys));
		is.read(responseLine);
		if (responseLine != null) {
			Message extented = Message.receiveMessage(responseLine, keys);
			if (extented.cmd != Message.Cmd.EXTENDED)
				throw new IOException("Incorrect message");
			int key = extented.data[0];
			System.out.println("Successfully EXTENDED connection with "+host+":"+port+" Using Cipher Key "+key);
			return key;
			
		}
		return -1;
	}

	public void initDataTransfer(byte[] responseLine) throws IOException {
		Message request = new Message();

		request.type = Message.CellType.PROXY;
		request.cmd = Message.Cmd.BEGIN;
		request.data = (serverHostname + ":" + serverPort + "").getBytes();

		// for (byte mess : create.createMeassage()){
		os.write(request.createMessage(hop1Key,hop2Key));
		is.read(responseLine);
		if (responseLine != null) {
			Message response = Message.receiveMessage(responseLine,hop1Key,hop2Key);
			if (response.cmd != Message.Cmd.CONNECTED)
				throw new IOException("Incorrect message! Connected expected.");
			connected=System.currentTimeMillis();
			System.out.println("Successfully CONNECTED  with "+serverHostname);
			System.out.println("Website Connection time: " + (connected-dir) +" ms");
		}
	}

	public void generateMessage(byte[] responseLine) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String text = "GET / HTTP/1.1\r\n"; // HTTP connection
		text += "Host: " + serverHostname + "\r\n\r\n";
		ArrayList<Message> messages = new ArrayList<>();
		Message response = new Message();
		// Message.padding().createMessage();

		for (Message request : Message.decompose(text)) {
			response = request;
			os.write(request.createMessage(hop1Key,hop2Key));
			// os.flush();
			if (request.cmd != Message.Cmd.END) {
			}

		}
		// os.write(response.createMessage());
		//System.out.println("Encoding request:");
		//System.out.println(text);

		// while(true){
		
		do {
			is.read(responseLine);
			response = Message.receiveMessage(responseLine, hop1Key,hop2Key);

			if (response.cmd == Message.Cmd.END) {
				//System.out.println("got end");
				break;

			}
			/* trash */responseLine[2] = (byte) Message.Cmd.KEEP_ALIVE_PADDING.ordinal(); os.write(responseLine);
			//System.out.println("Got block of data of legth");
			messages.add(response);
		} while (true);
		// }
		//System.out.println("closing");
		is.close();
		os.close();
		
		text = Message.compose(messages);
		//System.out.println(text);
		PrintWriter writer = new PrintWriter(saveFileName, "UTF-8");
		writer.print(text);
	    writer.close();
	    System.out.println("Wrote to file '"+saveFileName+"'");
	    full=System.currentTimeMillis();
		System.out.println("Total Response  time: " + (full-dir) +" ms");
	}

	public void startConnection() {

		/* declaration of client socket and io streams */
		Socket clientSocket = null;

		BufferedReader isUser = null;
		// String responseLine, userLine;
		byte[] responseLine = new byte[512];
		try {
			clientSocket = new Socket(hop1Hostname, hop1port);
			System.out.println("Created socket for " + hop1Hostname);
			os = new DataOutputStream(clientSocket.getOutputStream());
			is = new DataInputStream(clientSocket.getInputStream());
		} catch (UnknownHostException e) {
			System.err.println("Error: Unknown hostname.");
		} catch (IOException e) {
			System.err.println("Error: Unreachable host.");
		}

		if (clientSocket != null && os != null && is != null) {
			try {
				initProxyConn(responseLine);
				hop2Key = initExtendedProxyConn(responseLine, hop2Hostname, hop2port, hop1Key);
//				hop3Key = initExtendedProxyConn(responseLine, hop3Hostname, hop3port);

				initDataTransfer(responseLine);
				generateMessage(responseLine);

			} catch (UnknownHostException e) {
				System.err.println("Trying to connect to unknown host: " + e);
			} catch (IOException e) {
				System.err.println("IOException:  " + e);
				// break;
			}
		}

		/* close io streams and opened sockets */
		try {
			clientSocket.close();
		} catch (UnknownHostException e) {
			System.err.println("Trying to connect to unknown host: " + e);
		} catch (IOException e) {
			System.err.println("IOException:  " + e);
		}
		successfulConnection = true;
	}
	
	
	String directoryServer = "35.160.134.241";
	int directoryServerPort = 8080;

	String saveFileName ="index.html";
	
	public void chooseHops(){
		try {
			Socket dirSocket = new Socket(directoryServer,directoryServerPort);
			BufferedReader is = new BufferedReader(new InputStreamReader(dirSocket.getInputStream()));
			PrintWriter	os = new PrintWriter(dirSocket.getOutputStream(), true);
			
			os.println("client");
			while((dir-start< 30*1000)){
			hop1Hostname =  is.readLine();
			hop2Hostname =  is.readLine();
			hop3Hostname =  is.readLine();
			dirSocket.close();
			dir =System.currentTimeMillis();
			if(hop1Hostname!=null && hop2Hostname != null){
			System.out.println("Time taken to connect to directory and choose hops:"+(dir-start)+" ms");
			break;
			}
			}
			System.err.println("Couldn't find any hops");
			System.exit(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	static long start, dir, connected, full;
	public static void main(String[] args) {
		start =System.currentTimeMillis();
		dir=start;
		Client c = new Client();
		if (args.length == 1 || args.length == 2) {

//			c.hop1Hostname = "35.162.165.63"; // args[0];
//			c.hop1port = 8080; // Integer.parseInt(args[1]);
//			c.hop2Hostname = "35.161.60.16";// args[2];
//			c.hop2port = 8080;// Integer.parseInt(args[3]);
			c.serverHostname = args[0];// "www.google.com";
			c.chooseHops();
			c.serverPort = 80;
			if (args.length == 2){
				c.saveFileName=args[1];
			}
		}
		if (args.length >= 4) {

			c.hop1Hostname = args[0];
			c.hop1port = Integer.parseInt(args[1]);
			c.hop2Hostname = args[2];
			c.hop2port = Integer.parseInt(args[3]);
//			c.hop3Hostname = args[4];
//			c.hop3port = Integer.parseInt(args[5]);
			c.serverHostname = "www.google.com";
			c.serverPort = 80;
		}
//		c.directoryServer=("localhost");
//		c.chooseHops();
//		System.out.println(c.hop1Hostname+c.hop2Hostname);
		
		c.startConnection();
		

	}

}
