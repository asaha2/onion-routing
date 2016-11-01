import java.io.*;
import java.net.*;

import javax.xml.stream.events.StartDocument;

/* usage: java Proxy <hostname> <port-number> */

public class Server{
	int port;
	
	public Server(int port) {
		this.port = port;
	}
	
	public void startConnection() {

		/* declaration of server-client socket and io streams */
		ServerSocket echoServer = null;
		Socket serverSocket = null;
		BufferedReader is = null;
		PrintWriter os;
		String line;
		
		/* open socket on port xxxx, needs to be more than 1023 if not privileged users */ 
		try{
//			port = Integer.parseInt(args[0]);
			echoServer = new ServerSocket(port);
		} catch(IOException e){
			System.out.println("Error: Failed to initialize socket");
		}
		
		/* listen and accept socket connections, initialize io streams, 
		   echo data back to client as long as receiving data */ 
		try{
			serverSocket = echoServer.accept();
			is = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
			os = new PrintWriter(serverSocket.getOutputStream(), true);
			while(true){
				line = is.readLine();
				while(line != null){
					System.out.println("Received from proxy: " + line);
					// os.println(line);
					line = null;
				}
			}
		} catch(IOException e){
			System.out.println("Error: Failed to accept socket connections");
		}
	}
}
