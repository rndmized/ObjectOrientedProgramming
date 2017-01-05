package ie.gmit.sw;

import java.io.File;
import java.io.FileReader;
//We need the Java IO library to read from the socket's input stream and write to its output stream
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
//Sockets are packaged in the java.net library
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

import ie.gmit.sw.XMLParser.Context;


public class WebClient implements Runnable { //The class WebClient must be declared in a file called WebClient.java
	
	private Context ctx;
	private volatile boolean finish;
	private Scanner stdin;	
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	public WebClient(Context ctx){
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
						requestFileList();
						break;
					case 3:
						requestFile();
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
			/*
			String request = "GET request";
			//Serialise / marshal a request to the server
			ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(request); //Serialise
			out.flush(); //Ensure all data sent by flushing buffers
			
			Thread.yield(); //Pause the current thread for a short time (not used much)
			
			//Deserialise / unmarshal response from server 
			ObjectInputStream in = new ObjectInputStream(s.getInputStream());
			String response = (String) in.readObject(); //Deserialise
			System.out.println(response);
			
			*/

	}//End of run(). The thread will now die...sob..sob...;)
	
	private Socket connect(){
		//Connect to the server
		try { //Attempt the following. If something goes wrong, the flow jumps down to catch()
			Socket sock = new Socket(ctx.getHost(), ctx.getPort()); //Connect to the server
			//Serialise / marshal a request to the server
			out = new ObjectOutputStream(sock.getOutputStream());
			String request = "Requesting Connection from " + ctx.getUsername()  + ".";
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
	
	private void sendMessage(String msg) {
		try {
			out.writeObject(msg);
			out.flush();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	private void requestFile(){
		System.out.println("Please enter file name: ");
		String file_request = stdin.next();
		sendMessage(file_request);
		
		File response;
		try {
			response = (File) in.readObject();
			System.out.println(response);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //Deserialise
		
		
		
	}
	
	private void requestFileList(){
		
		try { //Attempt the following. If something goes wrong, the flow jumps down to catch()
			
			//Serialise / marshal a request to the server
			String request = "Requesting File List from " + ctx.getUsername()  + ".";
			sendMessage(request);
			
			//Deserialise / unmarshal response from server 
			List response = (List) in.readObject(); //Deserialise
			System.out.println(response);

		} catch (Exception e) { //Deal with the error here. A try/catch stops a programme crashing on error  
			System.out.println("Error: " + e.getMessage());
		}//End of try /catch
		
	}
}//End of class
	
