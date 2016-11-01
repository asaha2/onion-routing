import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.HashSet;


public class DirectoryServer {
	HashMap<String, String> proxies;
	int port = 80; 		//whatever
	
	public void addProxyServer(String serverName, String hostname) {
		proxies.put(serverName, hostname);
	}
	
	public DirectoryServer() {
		proxies = new HashMap<String, String>();
		addProxyServer("Proxy1", "35.162.165.63");
		addProxyServer("Proxy2", "35.161.60.16");
		addProxyServer("Proxy3", "35.162.181.227");
		addProxyServer("Proxy4", "35.162.176.50");
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
		Client client = new Client(IPAddress, port);
		client.startConnection();

		if (client.successfulConnection) {
			return true;
		}
		
		return false;
	}
	
	
	public static void main(String args[]) {
		DirectoryServer dirServer = new DirectoryServer();
	}
}