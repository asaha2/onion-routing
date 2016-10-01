import java.io.*;
import java.net.*;
public class Client{
	public static void main(String args[]){
		
		/* declaration of client socket and io streams */ 
		Socket smtpSocket = null;
		DataOutputStream os = null;
		// DataInputStream is = null;
		BufferedReader is = null;
		
		/* open socket in localhost on port 1234, initialize io streams */ 
		try{
			smtpSocket = new Socket("localhost", 1234);
			os = new DataOutputStream(smtpSocket.getOutputStream());
			// is = new DataInputStream(smtpSocket.getInputStream());
			is = new BufferedReader(new InputStreamReader(smtpSocket.getInputStream()));
		} catch(UnknownHostException e){
			System.err.println("Error: Unknown hostname.");
		} catch(IOException e){
			System.err.println("Error: Unreachable host.");
		}
		
		/* transmit data via opened sockets */ 
		if(smtpSocket != null && os != null && is != null){
			try{
				// The capital string before each colon has a special meaning to SMTP
				// you may want to read the SMTP specification, RFC1822/3
				os.writeBytes("HELO\n");
				os.writeBytes("MAIL From: k3is@fundy.csd.unbsj.ca\n");
				os.writeBytes("RCPT To: k3is@fundy.csd.unbsj.ca\n");
				os.writeBytes("DATA\n");
				os.writeBytes("From: k3is@fundy.csd.unbsj.ca\n");
				os.writeBytes("Subject: testing\n");
				os.writeBytes("Hi there\n"); // message body
				os.writeBytes("\n.\n");
				os.writeBytes("QUIT");
				
				/* read until receving 'Ok' from server, break once received */
				String responseLine;
				while((responseLine = is.readLine()) != null){
					System.out.println("Server: " + responseLine);
					if(responseLine.indexOf("Ok") != -1) break;
				}
				
				/* close io streams and opened sockets */
				os.close();
				is.close();
				smtpSocket.close();
			} catch (UnknownHostException e){
				System.err.println("Trying to connect to unknown host: " + e);
			} catch (IOException e){
				System.err.println("IOException:  " + e);
			}
		}
	}
}
