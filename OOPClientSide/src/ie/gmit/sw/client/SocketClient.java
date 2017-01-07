package ie.gmit.sw.client;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
//We need the Java IO library to read from the socket's input stream and write to its output stream
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
//Sockets are packaged in the java.net library
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import ie.gmit.sw.config.Context;
import ie.gmit.sw.request.Request;


public class SocketClient implements Runnable { //The class WebClient must be declared in a file called WebClient.java
	
	private Context ctx;
	private volatile boolean finish;
	private Scanner stdin;	
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	public SocketClient(Context ctx){
		this.ctx =ctx;
		stdin = new Scanner(System.in);
	}
	
	
	public void run() { 
			System.out.println("Starting to run!");
			finish = false;
			String threadName = Thread.currentThread().getName(); 
			Socket socket = null;
			
			do {
				System.out.println("User " + threadName );
				System.out.println("1. Connect to Server.");
				System.out.println("2. Print file Listing.");
				System.out.println("3. Download File.");
				System.out.println("4. Quit.");
				System.out.println("Type Option [1-4]>");
				int choice = 0;
				
				try {
					choice = stdin.nextInt();	
				} catch (Exception e) {
					stdin.nextLine();
				}
				
				switch(choice){
					case 1:
						socket = connect();
						break;
					case 2:
						requestFileList(socket);
						break;
					case 3:
						requestFile(socket);
						break;
					case 4:
						finish = true;
						disconnect(socket);
						break;
					default:
						System.out.println("Invalid option.");
						break;
				}		
			}while(!finish);
			
	}//End of run(). The thread will now die...sob..sob...;)
	/**
	 * 
	 * 
	 * */
	
	private Socket connect(){
		//Connect to the server
		try { //Attempt the following. If something goes wrong, the flow jumps down to catch()
			Socket sock = new Socket(ctx.getHost(), ctx.getPort()); //Connect to the server
			//Serialise / marshal a request to the server
			out = new ObjectOutputStream(sock.getOutputStream());
			Request request = new Request("Connection", sock.getLocalAddress().toString(),new Date());
			sendMessage(request);
			
			Thread.yield(); //Pause the current thread for a short time (not used much)
			
			//Deserialise / unmarshal response from server 
			in = new ObjectInputStream(sock.getInputStream());
			String response = (String) in.readObject(); //Deserialise
			System.out.println(response);
			return sock;

		} catch (Exception e) { //Deal with the error here. A try/catch stops a programme crashing on error  
			System.out.println("Error: " + e.getMessage());
			return null;
		}//End of try /catch
	}
	private void disconnect(Socket s){
		try {
			if(s != null && s.isConnected()){
				s.close();
			}
		} catch (IOException e) {
			System.out.println("No connection available.");
		} //Tidy up
	}
	private void sendMessage(Object msg) {
		try {
			out.writeObject(msg);
			out.flush();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	private void requestFile(Socket s){
		int bytesRead;
	    int current = 0;
	    byte [] mybytearray = null;
	    FileOutputStream fos;
	     BufferedOutputStream bos = null;
	    
		System.out.println("Please enter file name: ");
		String file_request = stdin.next();
		Request request = new Request("File request", s.getLocalAddress().toString(),new Date());
		request.setFilename(file_request);
		sendMessage(request);

		try {
			int size = (Integer) in.readObject();
			mybytearray  = new byte [size];
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// receive file

		try {
			fos = new FileOutputStream(ctx.getDownload_dir()+"/"+file_request);
			bos = new BufferedOutputStream(fos);
		      bytesRead = in.read(mybytearray,0,mybytearray.length);
		      current = bytesRead;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      do {
	         try {
				bytesRead = in.read(mybytearray, current, (mybytearray.length-current));
				if(bytesRead >= 0) current += bytesRead;
			} catch (IOException e) {
				e.printStackTrace();
			}
	      } while(current < mybytearray.length);
	      try {
	    	  bos.write(mybytearray, 0 , current);
			bos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	   }

	
	private void requestFileList(Socket s){
		
		try { //Attempt the following. If something goes wrong, the flow jumps down to catch()
			
			//Serialise / marshal a request to the server
			Request request = new Request("Listing", s.getLocalAddress().toString(), new Date());
			sendMessage(request);
			//Deserialise / unmarshal response from server 
			List response = (List) in.readObject();
			for (int i = 0; i < response.size(); i++) {
				System.out.println(response.get(i));
			}//Deserialise
		} catch (Exception e) { //Deal with the error here. A try/catch stops a programme crashing on error  
			System.out.println("Error: " + e.getMessage());
		}//End of try /catch
		
	}
}//End of class
	
