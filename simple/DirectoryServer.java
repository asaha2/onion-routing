import java.io.*;
import java.net.*;
import java.util.HashMap;


public class DirectoryServer {
	HashMap<String, String> proxies;
	
	public void addProxyServer(String serverName, String hostname) {
		proxies.put(serverName, hostname);
	}
	
	public DirectoryServer() {
		proxies = new HashMap<>();
		addProxyServer("Proxy1", "35.162.165.63");
		addProxyServer("Proxy2", "35.161.60.16");
		addProxyServer("Proxy3", "35.162.181.227");
		addProxyServer("Proxy4", "35.162.176.50");
	}
	
	public HashMap<String, String> requestProxies() {
		return proxies;
	}
	
	public static void main(String args[]) {
		DirectoryServer dirServer = new DirectoryServer();
	}
}