package simple;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.UnknownHostException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import com.sun.xml.internal.bind.v2.Messages;

import simple.Message;

/* usage: java Proxy <hostname> <source-port> <destination-port> */

public class Proxy extends Thread {
	int recvPort, sendPort;
	String hostname;
	String message;
	boolean isExit=true;
	Thread t;
	PrintWriter serverWriter;


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

//	public void start() {
//		System.out.println("Starting proxy: " + hostname);
//		if(t == null) {
//			t = new Thread("Thread-" + hostname);
//			t.start();
//		}
//	}


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

	private void relayMessage(Message m, byte[] responseLine) throws IOException {
		outputForward.write(m.createMessage());// ask next
		inputForward.read(responseLine);
		m = Message.receiveMessage(responseLine);
		outputBackward.write(m.createMessage());
	}

	private void extendProxyConn(Message m) throws IOException {
		// System.out.println("User input: " + userLine);
		isExit = false;
		byte[] responseLine = new byte[512];
		if (null != forwardProxy) {
			relayMessage(m, responseLine);
		}

		System.out.println(
				"got extend from " + backwardProxy.getInetAddress().toString() + " " + backwardProxy.getPort());

		Message create = new Message();
		create.type = Message.CellType.control;
		create.cmd = Message.Cmd.create;
		String[] host = (new String(m.data)).split(":");
		forwardProxy = new Socket(host[0], new Integer(host[1]));
		inputForward = new DataInputStream(forwardProxy.getInputStream());
		outputForward = new DataOutputStream(forwardProxy.getOutputStream());
		// for (byte mess : create.createMeassage()){
		outputForward.write(create.createMessage());
		inputForward.read(responseLine);
		if (responseLine != null) {
			Message created = Message.receiveMessage(responseLine);

			if (created.cmd != Message.Cmd.created)
				throw new IOException("Incorrect message");

			System.out.println("Got created from 2nd hop ");
		}

		Message extended = new Message();
		extended.type = Message.CellType.proxy;
		extended.cmd = Message.Cmd.extended;

		// for (byte mess : create.createMeassage()){
		outputBackward.write(extended.createMessage());
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
		if (m.cmd == Message.Cmd.create) {
			Message created = new Message();

			created.type = Message.CellType.control;
			created.cmd = Message.Cmd.created;
			System.out.println("got create packet from " + backwardProxy.getInetAddress().toString() + " "
					+ backwardProxy.getPort());

			outputBackward.write(created.createMessage());

		}

	}

	public void handshake(Message m) throws NumberFormatException, UnknownHostException, IOException {
		if (null != forwardProxy) {
			relayMessage(m, new byte[512]);
			return;
		}

		String[] host = (new String(m.data)).split(":");


		forwardProxy = new Socket(host[0], new Integer(host[1]));// TCP
																	// connection

		inputForward = new DataInputStream(forwardProxy.getInputStream());
		outputForward = new DataOutputStream(forwardProxy.getOutputStream());
		Message connected = new Message();
		connected.type = Message.CellType.proxy;
		connected.cmd = Message.Cmd.connected;
		System.out.println(
				"got extend from " + backwardProxy.getInetAddress().toString() + " " + backwardProxy.getPort());

		outputBackward.write(connected.createMessage());

	}


	public void exchangeData(byte[] responseLine) throws IOException {
		PrintWriter writer = new PrintWriter(forwardProxy.getOutputStream());
		BufferedReader reader = new BufferedReader(new InputStreamReader(forwardProxy.getInputStream())),
				bleh= new BufferedReader(new InputStreamReader( inputBackward));
		Message resp;
		ArrayList<Message> messages = new ArrayList<>();
		String text;
		
		
		do{
			inputBackward.read(responseLine);
			resp = Message.receiveMessage(responseLine);
			System.out.println(Arrays.toString(responseLine));
			System.out.println(new String(responseLine,14,40));
			//if(resp.cmd == Message.Cmd.end) break;
			
			messages.add(resp);
		} while(bleh.ready());
		System.out.println(messages);
		text = Message.compose( messages);
		System.out.println(text);
		writer.print(text);
		writer.flush();
		
		String response = "";
		do{
			response += reader.readLine()+"\r\n";
	}
			while (reader.ready()); 
		
System.out.println(response);
		for (Message request : Message.decompose(response)) {
			outputBackward.write(request.createMessage());
		}

	}

	public void recieveMessage() {

		/*
		 * open socket on port xxxx, needs to be more than 1023 if not
		 * privileged users
		 */
		try {
			echoProxy = new ServerSocket(recvPort, 1);// maximum 1 connection

		} catch (IOException e) {
			System.out.println("Error: Failed to initialize socket" + e);
		}

		/*
		 * listen and accept socket connections, initialize io streams, echo
		 * data back to client as long as receiving data
		 */
		while (true) {
			try {
				acceptProxyConn();
				byte[] line = new byte[512];
				inputBackward.read(line);

				Message m = Message.receiveMessage(line);

				if (m.cmd == Message.Cmd.extend) {
					extendProxyConn(m);
					while (true) {
						inputBackward.read(line);
						System.out.println(Arrays.toString(line));
						m = Message.receiveMessage(line);
						relayMessage(m, line);
					}
				}
				if (m.cmd == Message.Cmd.begin) {
					handshake(m);
					exchangeData(line);
				} else {
					throw new IOException("Wrong type of message. begin Expected, obtained" + m.cmd);
				}

			} catch (IOException e) {
				System.out.println("Error: Failed to accept socket connections " + e);
			}
		}
	}



	public static void main(String[] args) {
		Proxy p = new Proxy(args[0], Integer.parseInt(args[1]));
		//p.start();

	
		p.recieveMessage();

		// p.setSendPort(8081);
	}
}