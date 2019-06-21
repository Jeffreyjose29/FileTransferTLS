import javax.net.ssl.*;
import java.security.*;
import java.security.cert.*;
import javax.naming.ldap.*;
import javax.net.*;
import java.io.*;
//import javax.net.ssl.SSLParameters;


public class MyTLSFileClient{
	
	public static String hostname;
	public static int port;
	public static String file;
	public static BufferedOutputStream bufferOutput;
	public static BufferedInputStream bufferInput;
	public static String response;
	public String message;
	public static String line;
	public static int serverdata;
	public static String transferredFile;

	
	public static void main (String[] args){
		try {
			getUserInput(args);					//Calling the userInput method to get the hostname, connected port and the filename to be transferred


			/*----------------------------------------SHAOQUN CODE-------------------------------------------------------------------*/
			SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();			//Create a SSLSocketFactory object and get the default ssl
			SSLSocket socket = (SSLSocket)factory.createSocket(hostname, port);					//Make a socket object that is connected to the hostname and the port (40202)

			SSLParameters params = new SSLParameters();											//Parameters for our socket 
			params.setEndpointIdentificationAlgorithm("HTTPS");									//Create and assign the parameters to the socket object created previously
			socket.setSSLParameters(params);			
			socket.startHandshake();															//Connect with the server
			
			
			/*get the X509Certificate for this session */
			SSLSession sesh = socket.getSession();
			X509Certificate cert = (X509Certificate)sesh.getPeerCertificates()[0];
			
			/* extract the CommonName, and the compare */
			String commonName = getCommonName(cert);
			System.out.println(commonName);
			
			/** at this point, can getInputStream and getOutputStream as you would a regular socket */
			
			//Set up the input and output for the socket
			bufferOutput = new BufferedOutputStream(socket.getOutputStream());
			bufferInput = new BufferedInputStream(socket.getInputStream());
			
			sendFile(file);									//Call the sendFile method and parse in the file to be transferred
			
			BufferedReader read = new BufferedReader(new InputStreamReader(socket.getInputStream()));			//Read the response back from the user
			
			response = read.readLine();				//Assign the response from the user to a string called 'response'
			
			if(response.equals("FILE NOT FOUND")){				//If it return "Error", the file cannot be located
				System.out.println("Testing: Error");		
			} else {
				recieve();							//Call the recieve method
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();					//Print out the error
		}
	} 
	
	public static void sendFile(String fileMessage) throws Exception {
		line = fileMessage + "\r\n";						//Adding a return and new line
		byte[] byteArray = line.getBytes("UTF-8");			//Convert everything read to bytes
		for(int i = 0; i < byteArray.length; i++) {			//For each byte in the array
			bufferOutput.write(byteArray[i]);				//write each bit
		}
		bufferOutput.flush();								//clear the bufferoutput
		return;												//exit the method
	}
	
	public static void recieve() throws Exception{
		transferredFile = "_" + file;							//Create a new file with the filname and _ to indicate the file
		File fileob = new File(transferredFile);						//Create a new file object and parse it the new file created
		fileob.createNewFile();											//Create the new file
		FileOutputStream fos = new FileOutputStream(fileob);			//Get the output stream of the file
		byte[] dataByte = new byte[512];								//Byte array of size 512
		while(true){													//Infinte loop
			int serverdata = bufferInput.read(dataByte);				//Read the data
			if(serverdata == -1) {										//If the data has not been read
				fos.close();											//Close the output stream
				System.exit(0);											//Exit the program
			} else {
				fos.write(dataByte, 0, serverdata);						//Write each data bit to the file
			}
		}
	}
		
	
	//Shaoqun Code
	public static String getCommonName(X509Certificate cert) throws Exception{
		String dn = cert.getSubjectX500Principal().getName();						//Get the DN 
		LdapName ln = new LdapName(dn);											//Make ldapName object using the DN
		String cn = null;	
		//for all rdn in ln														
		for(Rdn rdn : ln.getRdns()) {
			if("CN".equalsIgnoreCase(rdn.getType())) {								//if rdn is commonname
				cn = rdn.getValue().toString();										
			}		
		}
		return cn;																	//return cn
	}
	
	//Get the three user inputs
	public static void getUserInput(String[] args) throws Exception{
		if(args.length == 3){
			hostname = args[0];														//hostname = 1st argument
			port = Integer.parseInt(args[1]);										//port number = 2nd argument
			file = args[2];															//filename = 3rd argument
		}	
		else{
			System.out.println("Usage: <hostname> <port number> <filename>");		//Error message and close the program
			System.exit(0);
		}
	}
}
