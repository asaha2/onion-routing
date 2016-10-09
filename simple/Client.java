import java.io.*;
import java.net.*;
public class Client{
	public static void main(String args[]){
		
		/* declaration of client socket and io streams */ 
		Socket clientSocket = null;
		DataOutputStream os = null;
		BufferedReader is = null;
		String responseLine;
		
		/* open socket in localhost on port 1500, initialize io streams */ 
		try{
			clientSocket = new Socket("localhost", 1500);
			os = new DataOutputStream(clientSocket.getOutputStream());
			is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch(UnknownHostException e){
			System.err.println("Error: Unknown hostname.");
		} catch(IOException e){
			System.err.println("Error: Unreachable host.");
		}
		
		/* transmit data via opened sockets */ 
		if(clientSocket != null && os != null && is != null){
			try{
				os.writeBytes("Message\n");
				while((responseLine = is.readLine()) != null){
					System.out.println("Response from server: " + responseLine);
				}
				
				/* close io streams and opened sockets */
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
}
