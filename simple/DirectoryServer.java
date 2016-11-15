import java.util.HashMap;
import java.util.HashSet;
import simple.Client;
import simple.Proxy;

public class DirectoryServer {
	HashMap<String, String> proxies;
	HashMap<String, String> destinationServers;
	String directoryServer;
	
	int port = 80; 		//whatever
	
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
		proxies = new HashMap<String, String>();
		addServer("Proxy0", "35.162.165.63", "proxy");
		addServer("Proxy1", "35.161.60.16", "proxy");
		addServer("Proxy2", "35.162.181.227", "proxy");
		addServer("Proxy3", "35.162.176.50", "proxy");
		
		destinationServers = new HashMap<String, String>();
		addServer("DestinationServer1", "35.160.73.178", "destination");
		//add more destination servers here
		
		directoryServer = "35.160.134.241";
		
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
		Proxy[] prx = new Proxy[2];
		
		prx[0] = new Proxy("127.0.0.1", 8080);
		prx[1] = new Proxy("127.0.0.1", 8082);
		Client c = new Client(prx);
		
		prx[0].start();
		prx[1].start();
		c.start();
		
		System.out.println("Started");

	}
}