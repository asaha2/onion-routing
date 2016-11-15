package simple;
import java.io.*;
import java.net.*;
import simple.Message;

/* usage: java Client <hostname> <port-number> */

public class Client{
	String hop1Hostname,hop2Hostname,serverHostname;
	int hop1port,hop2port, serverPort=80;
	boolean successfulConnection = false;
	private DataOutputStream os = null;
	private DataInputStream is = null;
	
	public Client() {
	}
	
	private void initProxyConn(byte[] responseLine) throws IOException {
		// System.out.println("User input: " + userLine);
		Message create = new Message();
		create.type=Message.CellType.control;
		create.cmd=Message.Cmd.create;
		

		//for (byte mess : create.createMeassage()){
		os.write(create.createMessage()[0],0,512);
		is.readFully(responseLine,0,512);
		if(responseLine  != null){
			Message created = Message.receiveMessage(responseLine);
			if (created.cmd!=Message.Cmd.created) throw new IOException("Incorrect message");

			System.out.println("Response from server: " + created.data);							
		}
	}
	
	public void initExtendedProxyConn(byte[] responseLine) throws IOException {
		Message extend = new Message();
		extend.type=Message.CellType.proxy;
		extend.cmd=Message.Cmd.extend;
		extend.data =(hop2Hostname+":"+hop2port+"").getBytes();
		
		//for (byte mess : create.createMeassage()){
		os.write(extend.createMessage()[0],0,512);
		is.readFully(responseLine,0,512);
		if(responseLine  != null){
			Message extented = Message.receiveMessage(responseLine);
			if (extented.cmd!=Message.Cmd.extended) throw new IOException("Incorrect message");

			System.out.println("Response from server: " + extented.data);							
		}
	}
	
	public void initDataTransfer(byte[] responseLine) throws IOException {
		Message request = new Message();
		request.type=Message.CellType.proxy;
		request.cmd=Message.Cmd.data;
		request.data =(serverHostname+":"+serverHostname+"").getBytes();
		
		//for (byte mess : create.createMeassage()){
		os.write(request.createMessage()[0],0,512);
		is.readFully(responseLine,0,512);
		if(responseLine  != null){
			Message response = Message.receiveMessage(responseLine);
			//if (response.cmd!=Message.Cmd.extended) throw new IOException("Incorrect message");

			System.out.println("Response from server: " + response.data);							
		}
	}
	
	public void generateMessage() {
		
	}

	public void startConnection() {

		/* declaration of client socket and io streams */ 
		Socket clientSocket = null;
		
		BufferedReader isUser = null;
		//String responseLine, userLine;
		byte[] responseLine = new byte[512];
		
		/* open socket in hostname on port xxxx, initialize io streams */ 
		try{
			clientSocket = new Socket(hop1Hostname, hop1port);
			os = new DataOutputStream(clientSocket.getOutputStream());
			is = new DataInputStream(clientSocket.getInputStream());
		} catch(UnknownHostException e){
			System.err.println("Error: Unknown hostname.");
		} catch(IOException e){
			System.err.println("Error: Unreachable host.");
		}
		
		/* continually transmit data via opened sockets */ 
		if(clientSocket != null && os != null && is != null){
			//while(true){
				try{
					initProxyConn(responseLine);
					
					
					//}	
					
				} catch (UnknownHostException e){
					System.err.println("Trying to connect to unknown host: " + e);
				} catch (IOException e){
					System.err.println("IOException:  " + e);
					//break;
				}
			//}
		}

		/* close io streams and opened sockets */
		try{
			os.close();
			is.close();
			clientSocket.close();
		} catch (UnknownHostException e){
			System.err.println("Trying to connect to unknown host: " + e);
		} catch (IOException e){
			System.err.println("IOException:  " + e);
		}
		
		successfulConnection = true;
		
	}
	
	public static void main(String[] args) {
		Client c = new Client();
		c.hop2Hostname=c.hop1Hostname="127.0.0.1";
		c.hop1port=8080;
		c.hop2port=8082;
		c.serverHostname="www.google.com";
		c.serverPort=80;
		
		
		c.startConnection();
		// Make header +  message
		// get proxies
		// choose 2 proxies at random
		// sent create to 1st proxy
		// wait for the response
		// send extended create to 2nd proxy
		// wait for the extended response
		// Send the messane to proxy 1
		
	}
	
}
