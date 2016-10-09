import java.io.*;
import java.net.*;

/* usage: java Proxy <hostname> <source-port> <destination-port> */

public class Proxy{
	public static void main(String args[]){
		
		/* declaration of server-client socket and io streams */
		ServerSocket echoProxy = null;
		Socket preProxy = null;
		Socket postProxy = null;
		BufferedReader is = null;
		PrintWriter os;
		String line, hostname;
		int port1, port2;
		
		/* open socket on port xxxx, needs to be more than 1023 if not privileged users */ 
		try{
			hostname = args[0];
			port1 = Integer.parseInt(args[1]);
			port2 = Integer.parseInt(args[2]);
			echoProxy = new ServerSocket(port1);
			postProxy = new Socket(hostname, port2);
		} catch(IOException e){
			System.out.println("Error: Failed to initialize socket");
		}
		
		/* listen and accept socket connections, initialize io streams, 
		   echo data back to client as long as receiving data */ 
		try{
			preProxy = echoProxy.accept();
			is = new BufferedReader(new InputStreamReader(preProxy.getInputStream()));
			os = new PrintWriter(postProxy.getOutputStream(), true);
			while(true){
				line = is.readLine();
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