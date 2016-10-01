import java.io.*;
import java.net.*;
public class Server{
	public static void main(String args[]){
		
		/* declaration of server-client socket and io streams */
		ServerSocket echoServer = null;
		String line;
		// DataInputStream is;
		BufferedReader is = null;
		PrintWriter os;
		Socket clientSocket = null;
		
		/* open socket on port 1234, needs to be more than 1023 if not privileged users */ 
		try{
			echoServer = new ServerSocket(1234);
		} catch(IOException e){
			System.out.println("Error: Failed to initialize socket");
		}
		
		/* listen and accept socket connections, initialize io streams, 
		   echo data back to client as long as receiving data */ 
		try{
			clientSocket = echoServer.accept();
			// is = new DataInputStream(clientSocket.getInputStream());
			is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			os = new PrintWriter(clientSocket.getOutputStream(), true);
			while(true){
				line = is.readLine();
				System.out.println("Received: " + line);
				os.println(line);
			}
		} catch(IOException e){
			System.out.println("Error: Failed to accept socket connections");
		}
	}
}
