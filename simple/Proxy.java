import java.io.*;
import java.net.*;

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
		BufferedReader is = null;
		String line;
		
		/* open socket on port xxxx, needs to be more than 1023 if not privileged users */ 
		try{
			echoProxy = new ServerSocket(recvPort);
		} catch(IOException e){
			System.out.println("Error: Failed to initialize socket");
		}
		
		/* listen and accept socket connections, initialize io streams, 
		   echo data back to client as long as receiving data */ 
		try{
			preProxy = echoProxy.accept();
			is = new BufferedReader(new InputStreamReader(preProxy.getInputStream()));
			while(true){
				line = is.readLine();
				while(line != null){
					 System.out.println("Received from client: " + line);
					line = null;
				}
			}
		} catch(IOException e){
			System.out.println("Error: Failed to accept socket connections");
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
}