import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import simple.Client;
import simple.Proxy;

public class DirectoryServer {
	TreeMap<String, String> proxies;
	HashMap<String, String> destinationServers;
	String directoryServer;
	
	int port = 8080; 		//whatever
	
	public void addServer(String serverName, String hostname, String serverType) {
		if (serverType.equals("proxy")) {
			proxies.put(serverName, hostname);
		}
		
		else if (serverType.equals("destination")) {
			destinationServers.put(serverName, hostname);
		}
	}
	
	/*
	 * 	Initialize: proxies, destinationServers, directoryServer
	 */
	
	public DirectoryServer() {
		proxies = new TreeMap<String, String>();
		addServer("Proxy1", "35.162.165.63", "proxy");
		addServer("Proxy2", "35.161.60.16", "proxy");
		addServer("Proxy4", "35.162.176.50", "proxy");
		addServer("Proxy3", "35.162.181.227", "proxy");

		
		destinationServers = new HashMap<String, String>();
		addServer("DestinationServer1", "35.160.73.178", "destination");
		//add more destination servers here
		
		directoryServer = "35.160.134.241";
		
	}
	
	public void run() throws Exception{
		ServerSocket echoProxy;
		Socket serverSocket;
		BufferedReader is;
		PrintWriter os;
		try{
			echoProxy = new ServerSocket(port, 1);
		}catch(Exception e){
			throw e;
		}
		while (true){
			try {
				serverSocket = echoProxy.accept();
				is = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
				os = new PrintWriter(serverSocket.getOutputStream(), true);
				String line = is.readLine();
				if (null != line && line.equals("client")){
					for (String p: proxies.values()){
						os.println(p);
					}
				}
				if (null != line && line.equals("proxy")){
					 is.readLine();
					 if (null != line ){
						 addServer("Proxy"+proxies.size(), line, "proxy");
					 }
					
				}
				
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();				
			}
			
		}
	}
	//method will return list of unique available IP addresses of destination server
	public HashSet<String> requestDestinationServers() {
		
		HashSet<String> availableDestinationServers = new HashSet<String>();
		
		if (isAvailable(destinationServers.get("DestinationServer1"), port)) {
			availableDestinationServers.add(destinationServers.get("DestinationServer1"));
		}
		
		return availableDestinationServers;
	}
	
	
	//method will return list of unique available IP addresses of proxies
	public HashSet<String> requestProxies() {
		
		HashSet<String> availableProxies = new HashSet<String>();
		
		if (isAvailable(proxies.get("Proxy1"), port)) {
			availableProxies.add(proxies.get("Proxy1"));
		}
		
		if (isAvailable(proxies.get("Proxy2"), port)) {
			availableProxies.add(proxies.get("Proxy2"));
		}
		
		if (isAvailable(proxies.get("Proxy3"), port)) {
			availableProxies.add(proxies.get("Proxy3"));
		}
		
		if (isAvailable(proxies.get("Proxy4"), port)) {
			availableProxies.add(proxies.get("Proxy4"));
		}
		
		return availableProxies;
	}
	
	
	//method will check if a proxy is busy or not by establishing a connection
	public boolean isAvailable(String IPAddress, int port) { 
		Client client = new Client();
		client.startConnection();

		if (client.isConnSuccessful()) {
			return true;
		}
		
		return false;
	}
	
	
	
	
	public String getDirectoryServer() {
		return directoryServer;
	}
	
	public static void main(String args[]) {
	DirectoryServer dirServer = new DirectoryServer();
	while(true){try{		System.out.println("Started");

	dirServer.run();
	}catch (Exception e){e.printStackTrace(System.err);}}
//		Proxy[] prx = new Proxy[2];
//		
//		prx[0] = new Proxy("35.162.165.63", 8080);
//		prx[1] = new Proxy("35.161.60.16", 8082);
//		Client c = new Client(prx);
//		
//		prx[0].start();
//		prx[1].start();
//		c.start();
//		
		

	}
}