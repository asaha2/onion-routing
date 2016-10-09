import java.io.*;
import java.net.*;
public class Server{
	public static void main(String args[]){
		
		/* declaration of server-client socket and io streams */
		ServerSocket echoServer = null;
		Socket serverSocket = null;
		BufferedReader is = null;
		PrintWriter os;
		String line;
		
		/* open socket on port 1500, needs to be more than 1023 if not privileged users */ 
		try{
			echoServer = new ServerSocket(1500);
		} catch(IOException e){
			System.out.println("Error: Failed to initialize socket");
		}
		
		/* listen and accept socket connections, initialize io streams, 
		   echo data back to client as long as receiving data */ 
		try{
			serverSocket = echoServer.accept();
			// is = new DataInputStream(serverSocket.getInputStream());
			is = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
			os = new PrintWriter(serverSocket.getOutputStream(), true);
			while(true){
				line = is.readLine();
				System.out.println("Received from client: " + line);
				os.println(line);
			}
		} catch(IOException e){
			System.out.println("Error: Failed to accept socket connections");
		}
	}
}
