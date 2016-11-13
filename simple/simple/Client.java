package simple;
import java.io.*;
import java.net.*;
import simple.Message;

/* usage: java Client <hostname> <port-number> */

public class Client{
	String hostname;
	int port;
	boolean successfulConnection = false;
	
	public Client(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}
	
	public void initProxyConn() {
		
	}
	
	public void initExtendedProxyConn() {
	
	}
	
	public void initDataTransfer() {
		
	}
	
	public void generateMessage() {
		
	}
	
	public void startConnection() {

		/* declaration of client socket and io streams */ 
		Socket clientSocket = null;
		DataOutputStream os = null;
		DataInputStream is = null;
		BufferedReader isUser = null;
		//String responseLine, userLine;
		byte[] responseLine = new byte[512];
		
		/* open socket in hostname on port xxxx, initialize io streams */ 
		try{
			clientSocket = new Socket(hostname, port);
			os = new DataOutputStream(clientSocket.getOutputStream());
			is = new DataInputStream(clientSocket.getInputStream());
		} catch(UnknownHostException e){
			System.err.println("Error: Unknown hostname.");
		} catch(IOException e){
			System.err.println("Error: Unreachable host.");
		}
		
		/* continually transmit data via opened sockets */ 
		if(clientSocket != null && os != null && is != null){
			while(true){
				try{
					// System.out.println("User input: " + userLine);
					Message create = new Message();
					create.type=Message.CellType.control;
					create.cmd=Message.Cmd.create;

					//for (byte mess : create.createMeassage()){
					os.write(create.createMessage()[0],0,512);
					is.readFully(responseLine,0,512);
					if(responseLine  != null){
						Message created = Message.receiveMessage(responseLine);
						if (created.cmd==Message.Cmd.created) break;

						System.out.println("Response from server: " + created.data);							
					}			
					//}	
					
				} catch (UnknownHostException e){
					System.err.println("Trying to connect to unknown host: " + e);
				} catch (IOException e){
					System.err.println("IOException:  " + e);
					break;
				}
			}
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
		Client c = new Client("localhost",8080);
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
