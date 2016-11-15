package simple;
import java.io.*;
import java.net.*;
import simple.Message;

/* usage: java Proxy <hostname> <source-port> <destination-port> */

public class Proxy extends Thread {
	int recvPort, sendPort;
	String hostname;
	String message;
	boolean isExit=true;
	Thread t;

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

	DataInputStream inputBackward = null,inputForward;
	DataOutputStream outputBackward = null,outputForward;


	private void relayMessage(Message m, byte[] responseLine) throws IOException{
		outputForward.write(m.createMessage()[0]);//ask next 
		inputForward.readFully(responseLine);
		m=Message.receiveMessage(responseLine);
		outputBackward.write(m.createMessage()[0]);
	}

	private void extendProxyConn(Message m) throws IOException {
		// System.out.println("User input: " + userLine);
		isExit=false;
		byte[] responseLine= new byte[512];
		if (null!=forwardProxy){
			relayMessage(m,responseLine);
		}
		System.out.println("got extend from "+backwardProxy.getInetAddress().toString() +" "+backwardProxy.getPort());


		Message create = new Message();
		create.type=Message.CellType.control;
		create.cmd=Message.Cmd.create;
		String[] host= (new String(m.data)).split(":");
		forwardProxy = new Socket(host[0], new Integer(host[1]));
		inputForward= new DataInputStream(forwardProxy.getInputStream());
		outputForward= new DataOutputStream(forwardProxy.getOutputStream());
		//for (byte mess : create.createMeassage()){
		outputForward.write(create.createMessage()[0],0,512);
		inputForward.readFully(responseLine,0,512);
		if(responseLine  != null){
			Message created = Message.receiveMessage(responseLine);
			if (created.cmd!=Message.Cmd.created) throw new IOException("Incorrect message");

			System.out.println("Got created from 2nd hop ");	
		}

		Message extended = new Message();
		extended.type=Message.CellType.proxy;
		extended.cmd=Message.Cmd.extended;

		//for (byte mess : create.createMeassage()){
		outputBackward.write(extended.createMessage()[0]);
		//outputBackward.close();
		//inputBackward.close();
	}
	private void acceptProxyConn() throws IOException{
		backwardProxy = echoProxy.accept();
		byte[] line= new byte[512];
		inputBackward= new DataInputStream(backwardProxy.getInputStream());
		outputBackward= new DataOutputStream(backwardProxy.getOutputStream());

		//while(true){
		inputBackward.readFully(line,0,512);
		Message m = Message.receiveMessage(line);
		if (m.cmd==Message.Cmd.create){
			Message created = new Message();
			created.type=Message.CellType.control;
			created.cmd=Message.Cmd.created;
			System.out.println("got create packet from "+backwardProxy.getInetAddress().toString() +" "+backwardProxy.getPort());
			//for (byte mess : create.createMeassage()){
			outputBackward.write(created.createMessage()[0]);
			//outputBackward.close();
			//inputBackward.close();
		}

		inputBackward.readFully(line,0,512);
		m = Message.receiveMessage(line);
		if (m.cmd==Message.Cmd.extend){
			extendProxyConn(m);
			while (true){
				inputBackward.readFully(line,0,512);
				m = Message.receiveMessage(line);
				relayMessage(m,line);
			}
		}
		if (m.cmd==Message.Cmd.begin){
			sendData(m);		
		}else{throw new IOException("wrong type of message");}

	}
	public void sendData(Message m) throws NumberFormatException, UnknownHostException, IOException{
		if (null!=forwardProxy){
			relayMessage(m,new byte[512]);
			return;
		}
		String[] host= (new String(m.data)).split(":");

		forwardProxy = new Socket(host[0], new Integer(host[1]));//TCP connection

		inputForward= new DataInputStream(forwardProxy.getInputStream());
		outputForward= new DataOutputStream(forwardProxy.getOutputStream());
		PrintWriter pw = new PrintWriter(forwardProxy.getOutputStream());
		pw.print("GET / HTTP/1.1\r\n"); //HTTP connection
		pw.print("Host: "+forwardProxy.getInetAddress().getHostName()+"\r\n\r\n");
		pw.flush();

		System.out.println("waiting!");		

		byte b = inputForward.readByte();
		System.out.println(b);		

		Message connected = new Message();
		connected.type=Message.CellType.proxy;
		connected.cmd=Message.Cmd.connected;
		System.out.println("got extend from "+backwardProxy.getInetAddress().toString() +" "+backwardProxy.getPort());
		//for (byte mess : create.createMeassage()){
		outputBackward.write(connected.createMessage()[0]);
	}


	public void recieveMessage() {

		/* open socket on port xxxx, needs to be more than 1023 if not privileged users */ 
		try{
			echoProxy = new ServerSocket(recvPort,1);//maximum 1 connection

		} catch(IOException e){
			System.out.println("Error: Failed to initialize socket"+e);
		}

		/* listen and accept socket connections, initialize io streams, 
		   echo data back to client as long as receiving data */ 
		while(true){
			try{
				acceptProxyConn();


			} catch(IOException e){
				System.out.println("Error: Failed to accept socket connections "+e);
			}
		}
	}


	public static void main(String[] args) {
		Proxy p = new Proxy(args[0], Integer.parseInt(args[1]));
		p.start();
	}
}