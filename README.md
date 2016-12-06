Onion Routing: The Price of Anonymity
=====================================
An implementation of a small onion network.


Compilation instructions
------------------------

```
$ git clone https://github.com/asaha2/onion-routing.git
$ cd onion-routing/simple
$ javac simple/*
```

Running on our small onion network
------------------------
The onion network is available via proxies on AWS servers. Only supports HTTP and homepages of websites. 
You cannot choose which hops to use for the client and no logs of the proxies will be available to you. only clientside.
The
```
$ java simple/Client <website_url> <optional_file_name>
```

Running on localhost (with proxy logs)
------------------------
The onion network can be simulated on localhost for local testing purposes. 
This option lets you see whats going on at the proxies.
This option lets you specify the ports of the proxies.
However it only visits www.google.com
```
$ java simple/Proxy 127.0.0.1 8080
$ java simple/Proxy 127.0.0.1 8082
$ java simple/Client 127.0.0.1 8080 127.0.0.1 8082

```
