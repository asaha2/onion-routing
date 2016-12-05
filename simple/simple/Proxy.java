package simple;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

import simple.Message;
import simple.Message.Cmd;

/* usage: java Proxy <hostname> <source-port> <destination-port> */

public class Proxy extends Thread {
	int recvPort, sendPort;
	String hostname;
	String message;
	boolean isExit = true;
	Thread t;
	PrintWriter serverWriter;
	String directoryServer = "35.160.134.241";
	int directoryServerPort = 8080;
	int hopKeyBackwards=0;



	public Proxy(String hostname, int port) {
		this.setHostname(hostname);
		this.setRecvPort(port);
		this.setSendPort(port + 1);
	}

	public void run() {
		System.out.println("Receiving");
		this.recieveMessage();
		System.out.println(t.getName());
	}


	public int getRecvPort() {
		return this.recvPort;
	}

	public void setRecvPort(int port) {
		this.recvPort = port;
	}

	public void setSendPort(int port) {
		this.sendPort = port;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	ServerSocket echoProxy = null;

	Socket backwardProxy = null, forwardProxy = null;

	DataInputStream inputBackward = null, inputForward;
	DataOutputStream outputBackward = null, outputForward;

	
	public  void addToDirectory(){
		Socket dirSocket;
		PrintWriter os;
		try {
			dirSocket = new Socket("http://checkip.amazonaws.com/",directoryServerPort);
			 os = new PrintWriter(dirSocket.getOutputStream(), true);
			//HTTP GET
			dirSocket = new Socket(directoryServer,directoryServerPort);
			BufferedReader is = new BufferedReader(new InputStreamReader(dirSocket.getInputStream()));

			 os = new PrintWriter(dirSocket.getOutputStream(), true);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void extendProxyConn(Message m) throws IOException {
		// System.out.println("User input: " + userLine);
		isExit = false;
		byte[] responseLine = new byte[512];


		System.out.println(
				"Request to EXTEND connection from " + backwardProxy.getInetAddress().toString() + ":"
						+ backwardProxy.getPort());
		int hopKeyForwards = 0;
		Message create = new Message();
		create.type = Message.CellType.CONTROL;
		create.cmd = Message.Cmd.CREATE;
		String[] host = (new String(m.data)).split(":");
		forwardProxy = new Socket(host[0], new Integer(host[1]));
		inputForward = new DataInputStream(forwardProxy.getInputStream());
		outputForward = new DataOutputStream(forwardProxy.getOutputStream());
		// for (byte mess : create.createMeassage()){
		outputForward.write(create.createMessage());
		inputForward.read(responseLine);
		if (responseLine != null) {
			Message created = Message.receiveMessage(responseLine);
			
			if (created.cmd != Message.Cmd.CREATED)
				throw new IOException("Incorrect message");
			 hopKeyForwards = responseLine[3];
			System.out.println("Successfully CREATED connection with " + new String(m.data));
		}

		Message extended = new Message();
		extended.type = Message.CellType.PROXY;
		extended.cmd = Message.Cmd.EXTENDED;
		extended.data = new byte[1];
		extended.data[0] = (byte) hopKeyForwards;

		// for (byte mess : create.createMeassage()){
		outputBackward.write(extended.createMessage(hopKeyBackwards));
		// outputBackward.close();
		// inputBackward.close();
	}

	private void acceptProxyConn() throws IOException {
		backwardProxy = echoProxy.accept();
		byte[] line = new byte[512];
		inputBackward = new DataInputStream(backwardProxy.getInputStream());
		outputBackward = new DataOutputStream(backwardProxy.getOutputStream());

		// while(true){
		inputBackward.read(line);
		Message m = Message.receiveMessage(line);
		if (m.cmd == Message.Cmd.CREATE) {
			Message created = new Message();
			hopKeyBackwards = (new Random()).nextInt(50)+1;
			created.type = Message.CellType.CONTROL;
			created.cmd = Message.Cmd.CREATED;
			created.data = new byte[1];
			created.data[0] = (byte) hopKeyBackwards;
			System.out.println("Request to CREATE connection from  " + backwardProxy.getInetAddress().toString() + ":"
					+ backwardProxy.getPort());

			outputBackward.write(created.createMessage());

		}

	}

	public void handshake(Message m) throws NumberFormatException, UnknownHostException, IOException {

		String[] host = (new String(m.data)).split(":");

		forwardProxy = new Socket(host[0], new Integer(host[1]));// TCP
																	// connection
		inputForward = new DataInputStream(forwardProxy.getInputStream());
		outputForward = new DataOutputStream(forwardProxy.getOutputStream());
		Message connected = new Message();
		connected.type = Message.CellType.PROXY;
		connected.cmd = Message.Cmd.CONNECTED;
		System.out.println("Request to BEGIN connection from " + backwardProxy.getInetAddress().toString() + ":"
				+ backwardProxy.getPort() + " to " + new String(m.data));

		outputBackward.write(connected.createMessage(hopKeyBackwards));

	}
	

	public void exchangeData(byte[] responseLine) throws IOException {
		PrintWriter writer = new PrintWriter(forwardProxy.getOutputStream());
		BufferedReader reader = new BufferedReader(new InputStreamReader(forwardProxy.getInputStream())),
				bleh = new BufferedReader(new InputStreamReader(inputBackward));
		Message resp;
		ArrayList<Message> messages = new ArrayList<>();
		String text;
		// byte[] pad = Message.padding().createMessage();

		do {
			inputBackward.read(responseLine);
			resp = Message.receiveMessage(responseLine,hopKeyBackwards);

			if (resp.cmd == Message.Cmd.END)
				break;
			/* trash */
			responseLine[2] = (byte) Message.Cmd.KEEP_ALIVE_PADDING.ordinal();
			outputBackward.write(responseLine);
			messages.add(resp);

		} while (true);
		//System.out.println(messages);
		text = Message.compose(messages);
		System.out.println("Decoded message:");
		System.out.println(text);
		writer.print(text);
		writer.flush();

		String response = "";
		do {
			response += reader.readLine() + "\r\n";
		} while (reader.ready());
		System.out.println(response);
		//response = response + response + response;
		System.out.println("Received data from" + forwardProxy.getInetAddress().getHostName());
		for (Message request : Message.decompose(response)) {
			outputBackward.write(request.createMessage(hopKeyBackwards));
			if (request.cmd != Message.Cmd.END) {
				/* trash */ inputBackward.read(responseLine);
			}
		}
		System.out.println("End of communication");
	}
	
	private void relayMessage(Message m, byte[] responseLine) throws IOException {
		responseLine = m.createMessage();

		System.out.print("Message from Previous hop : " + m.cmd);
		if (m.cmd == Cmd.DATA)
			System.out.println(m.length);
		else
			System.out.println();
		
		//less encrypt
		outputForward.write(responseLine);// ask next
		inputForward.read(responseLine);
		m = Message.receiveMessage(responseLine);
		
		System.out.println("Message from Next hop : " + m.cmd);
		if (m.cmd == Cmd.DATA)
			System.out.println(" of length " + m.length + " bytes ");
		else
			System.out.println();

		outputBackward.write(m.createMessage(hopKeyBackwards)); //encrypt
	}
	public void recieveMessage() {
		
		while (true){//mulitple proxy connections

		try {
			if(null==echoProxy){
			echoProxy = new ServerSocket(recvPort, 1);// maximum 1 connection
			}

		} catch (IOException e) {
			System.out.println("Error: Failed to initialize socket");
			e.printStackTrace();
		}
		try {
			while (true) {
				System.out.println("Waiting for connections");

				acceptProxyConn();
				byte[] line = new byte[512];
				inputBackward.read(line);
//				System.out.println("woot"+hopKeyBackwards+ "\n"+new String(line,14,498));
				Message m = Message.receiveMessage(line,hopKeyBackwards);
//				System.out.println(new String(m.data));

				if (m.cmd == Message.Cmd.EXTEND) {
					extendProxyConn(m);//Extends to next hop
					while (true) {//relays till end of communication
						try {
							inputBackward.read(line);
//							System.out.println("woot"+hopKeyBackwards+ "\n"+new String(line,14,498));
							m = Message.receiveMessage( line, hopKeyBackwards); //decrypt
//							System.out.println(new String(m.data));
							relayMessage(m, line);
							
							if ((inputBackward.available()==0) && (inputForward.available()==0)){
								outputBackward.close();
								outputForward.close();
								System.out.println("Finished relaying");
								break;

							}
						} catch (IOException e) {
							System.out.println("Finished relaying");
							break;
						}
					}
				}

				if (m.cmd == Message.Cmd.BEGIN) {
					handshake(m);
					exchangeData(line);
					outputBackward.close();
					outputForward.close();
					break;
				} /*else {
					while(true);
					//throw new IOException("Wrong type of message. begin Expected, obtained" + m.cmd);

				}*/
			}
			System.out.println("Closing Sockets");
			forwardProxy.close();
			
			backwardProxy.close();
			backwardProxy=forwardProxy = null;
		} catch (IOException e) {
			// System.out.println("Error: Failed to accept socket
			// connections " + e);
			e.printStackTrace();
		}
		
		}

	}

	public static void main(String[] args) {
		int port = 8080;
		String host = null;
		if (args.length == 1) {
			port = Integer.parseInt(args[0]);
		}
		if (args.length == 2) {
			host = args[0];
			port = Integer.parseInt(args[1]);
		}
		Proxy p = new Proxy(host, port);
		System.out.println("New Proxy Server opened in port "+port);

//		System.out.println("Waiting for more connections");

		// p.start();

		p.recieveMessage();

		// p.setSendPort(8081);
	}
}
