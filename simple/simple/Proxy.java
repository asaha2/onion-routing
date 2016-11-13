package simple;
import java.io.*;
import java.net.*;
import simple.Message;

/* usage: java Proxy <hostname> <source-port> <destination-port> */

public class Proxy{
	int recvPort, sendPort;
	String hostname;
	String message;
	
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

	public void recieveMessage() {

		/* declaration of server-client socket and io streams */
		ServerSocket echoProxy = null;
		Socket preProxy = null;
		DataInputStream is = null;
		DataOutputStream os = null;

		
		/* open socket on port xxxx, needs to be more than 1023 if not privileged users */ 
		try{
			echoProxy = new ServerSocket(recvPort);

		} catch(IOException e){
			System.out.println("Error: Failed to initialize socket");
		}
		
		/* listen and accept socket connections, initialize io streams, 
		   echo data back to client as long as receiving data */ 
	while(true){
		try{
			preProxy = echoProxy.accept();
			byte[] line= new byte[512];
			is = new DataInputStream (preProxy.getInputStream());
			os = new DataOutputStream(preProxy.getOutputStream());
			//while(true){
				is.readFully(line,0,512);
				Message m = Message.receiveMessage(line);
				if (m.cmd==Message.Cmd.create){
					Message create = new Message();
					create.type=Message.CellType.control;
					create.cmd=Message.Cmd.create;
					System.out.println("got created from "+preProxy.getInetAddress().toString() +" "+preProxy.getPort());
					//for (byte mess : create.createMeassage()){
					os.write(create.createMessage()[0]);
					os.close();
					is.close();
					
					break;
				//}
//				while(line != null){
//					 System.out.println("Received from client: " + line);
//					line = null;
//				}
			}
		} catch(IOException e){
			System.out.println("Error: Failed to accept socket connections "+e.getMessage());
		}
		}
	}
	public void sendMessage() {

		/* declaration of server-client socket and io streams */
		ServerSocket echoProxy = null;
		Socket postProxy = null;
		PrintWriter os;
		String line = ""; //TODO generate this
		
		/* open socket on port xxxx, needs to be more than 1023 if not privileged users */ 
		try{
			echoProxy = new ServerSocket(recvPort);
			postProxy = new Socket(hostname, sendPort);
		} catch(IOException e){
			System.out.println("Error: Failed to initialize socket");
		}
		
		/* listen and accept socket connections, initialize io streams, 
		   echo data back to client as long as receiving data */ 
		try{
			os = new PrintWriter(postProxy.getOutputStream(), true);
			while(true){
				while(line != null){
					// System.out.println("Received from client: " + line);
					os.println(line);
					line = null;
				}
			}
		} catch(IOException e){
			System.out.println("Error: Failed to accept socket connections");
		}
	}
	
	public static void main(String[] args) {
		Proxy p = new Proxy();
		p.setHostname("localhost");
		p.setRecvPort(8080);
		p.recieveMessage();
		//p.setSendPort(8081);
		
	}
}