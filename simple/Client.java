import java.io.*;
import java.net.*;

/* usage: java Client <hostname> <port-number> */

public class Client{
	String hostname;
	int port;
	
	public Client(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}
	
	public void startConnection() {

		/* declaration of client socket and io streams */ 
		Socket clientSocket = null;
		DataOutputStream os = null;
		BufferedReader is = null;
		BufferedReader isUser = null;
		String responseLine, userLine;
		
		/* open socket in hostname on port xxxx, initialize io streams */ 
		try{
			clientSocket = new Socket(hostname, port);
			os = new DataOutputStream(clientSocket.getOutputStream());
			is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			isUser = new BufferedReader(new InputStreamReader(System.in));
		} catch(UnknownHostException e){
			System.err.println("Error: Unknown hostname.");
		} catch(IOException e){
			System.err.println("Error: Unreachable host.");
		}
		
		/* continually transmit data via opened sockets */ 
		if(clientSocket != null && os != null && is != null){
			while(true){
				try{
					userLine = isUser.readLine();
					// System.out.println("User input: " + userLine);
					if(userLine != null){
						os.writeBytes(userLine + "\n");
						userLine = null;
						responseLine = null;
						// if((responseLine = is.readLine()) != null){
						// 	System.out.println("Response from server: " + responseLine);
						// 	responseLine = null;
						// }				
					}
				} catch (UnknownHostException e){
					System.err.println("Trying to connect to unknown host: " + e);
				} catch (IOException e){
					System.err.println("IOException:  " + e);
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
	}
	
}
