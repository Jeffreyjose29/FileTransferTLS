import javax.net.ssl.*;
import java.security.*;
import javax.naming.ldap.*;
import javax.net.*;
import java.io.*;

public class FileManipulation extends Thread{
	
	public String brName;
	public SSLSocket s;
	public BufferedOutputStream outputStream;
	public BufferedReader br;
	public FileInputStream inputStream;
	
	public FileManipulation(String brName, SSLSocket s){
		this.brName = brName;
		this.s = s;
	}

	public void FileSend(String file) throws Exception {
		inputStream = new FileInputStream(file);
		
		byte[] dataByte = new byte[512];
		
		while(true) {
			int byteValue = inputStream.read(dataByte);			
			if(byteValue == -1) {
				inputStream.close();
				break;
			} else {
				outputStream.write(dataByte, 0, byteValue);
			}
		}
	}
	
	public void Message(String message) throws Exception {
		String writer = message + "\r\n";
		
		byte[] byteValue = writer.getBytes("UTF-8");
		
		for(int i = 0; i < byteValue.length; i++) {
			outputStream.write(byteValue[i]);
		}	
		outputStream.flush();
	}
	
	public void run() {		
		try{
			outputStream = new BufferedOutputStream(s.getOutputStream());
			
			if((new File(brName)).exists()) {
				Message("Accepted");
				FileSend(brName);
			} else {
				Message("FILE NOT FOUND");
			}
			
			outputStream.close();
		} catch(Exception ex) {
			System.out.println("FileManipulation: 'Run' Error");
		}
	}
}
