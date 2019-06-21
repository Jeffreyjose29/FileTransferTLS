import javax.net.ssl.*;
import java.security.*;
import javax.naming.ldap.*;
import javax.net.*;
import java.io.*;

public class MyTLSFileServer {
	
	static int port = 40202;					//port number that is used
	static String file;
	public static BufferedOutputStream bos;
	
	public static void main (String args[]) {
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			KeyStore ks = KeyStore.getInstance("JKS");
			
			char[] passphrase = "jeffy98".toCharArray();				//send the characters of my keystore password
			
			ks.load(new FileInputStream("server.jks"), passphrase);			//load the ks with file input of the server.jks
			
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");	//load the SunX509 algorithm
			kmf.init(ks, passphrase);
			
			ctx.init(kmf.getKeyManagers(), null, null);				//Set up ctx with null parameters
			
			ServerSocketFactory ssf = ctx.getServerSocketFactory();			//Get a serversocket object from the factory
			SSLServerSocket ss = (SSLServerSocket)ssf.createServerSocket(port);	//The serversocket is connected to our port number
			
			String[] EnabledProtocols = {"TLSv1.2", "TLSv1.1"};
			ss.setEnabledProtocols(EnabledProtocols);
			
			while(true) {
				try{
					SSLSocket s = (SSLSocket)ss.accept();				//Get a socket from the from the server-socket
					
					BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
					String brName = br.readLine();
					
					FileManipulation fm = new FileManipulation(brName, s);		//Create a new FileManipulation object and parse in SSLSocket and the values from reader
					
					fm.start();					//Calls the thread to begin execution
			} catch (SSLHandshakeException ex) {
					System.err.println("ERROR: TLS Handshake Failed Catch");	//The error message
				}
			}
			
		} catch (Exception e) {
			System.out.println("Error Test: Catch (main)");
			e.printStackTrace();
		}
	}
	
	/*public static void getPortNumber(String args[]) {
		if(args.length == 1) {
		}
	}*/
	
	/*public static void FileSend(String file) throws Exception {
		FileInputStream inputStream = new FileInputStream(file);
		
		byte[] dataByte = new byte[512];
		
		while(true) {
			int byteValue = inputStream.read(dataByte);
			
			if(byteValue == -1) {
				inputStream.close();
				break;
			} else {
				bos.write(dataByte, 0, byteValue);
			}
		}
	}
	
	public static void Message(String message) throws Exception {
		String line = message + "\r\n";
		
		byte[] byteValue = line.getBytes("UTF-8");
		for(int i = 0; i < byteValue.length; i++) {
			bos.write(byteValue[i]);
		}
		bos.flush();
		return;
	}*/
}

