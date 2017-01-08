
package ie.gmit.sw;

import java.io.BufferedOutputStream;
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

/**
 * This class implements a simple client side interface to connect to a file
 * server and download files from it.
 * 
 * @author RnDMizeD
 * @version 1.0
 */
public class SocketClient implements Runnable {

	private Context ctx;
	private volatile boolean finish;
	private Scanner stdin;
	private ObjectOutputStream out;
	private ObjectInputStream in;

	/**
	 * This constructs a SocketClient with a given context.
	 * 
	 * @param ctx
	 *            This is the context object containing the username, server
	 *            port, address, and local directory for downloads.
	 */
	public SocketClient(Context ctx) {
		this.ctx = ctx;
	}

	/**
	 * 
	 */
	public void run() {
		System.out.println("Starting to run!");
		finish = false;
		stdin = new Scanner(System.in);
		String threadName = Thread.currentThread().getName();
		Socket socket = null;

		do {
			System.out.println("User " + threadName);
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

			switch (choice) {
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
		} while (!finish);

	}// End of run(). The thread will now die...sob..sob...;)

	/**
	 * This method returns a socket connection with the server.
	 * 
	 * @return Socket
	 */
	private Socket connect() {
		// Connect to the server
		try { // Attempt the following. If something goes wrong, the flow jumps
				// down to catch()
			Socket sock = new Socket(ctx.getHost(), ctx.getPort()); // Connect
																	// to the
																	// server
			// Serialise / marshal a request to the server
			out = new ObjectOutputStream(sock.getOutputStream());
			Request request = new Request("Connection", sock.getLocalAddress().toString(), new Date());
			sendMessage(request);

			Thread.yield(); // Pause the current thread for a short time (not
							// used much)

			// Deserialise / unmarshal response from server
			in = new ObjectInputStream(sock.getInputStream());
			String response = (String) in.readObject(); // Deserialise
			System.out.println(response);
			return sock;

		} catch (Exception e) { // Deal with the error here. A try/catch stops a
								// programme crashing on error
			System.out.println("Error: " + e.getMessage());
			return null;
		} // End of try /catch
	}

	/**
	 * This method closes the current connection.
	 * 
	 * @param socket
	 */
	private void disconnect(Socket socket) {
		try {
			if (socket != null && socket.isConnected()) {
				socket.close();
			}
		} catch (IOException e) {
			System.out.println("No connection available.");
		} // Tidy up
	}

	/**
	 * This method takes an objects and sends it through socket connection.
	 * 
	 * @param object
	 *            The object to send.
	 */
	private void sendMessage(Object object) {
		try {
			out.writeObject(object);
			out.flush();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	/**
	 * This method requests a file from server and downloads it.
	 * 
	 * @param socket
	 *            The socket to make the request through.
	 */
	private void requestFile(Socket socket) {
		int bytesRead;
		int current = 0;
		int size = 0;
		byte[] mybytearray = null;
		FileOutputStream fos;
		BufferedOutputStream bos = null;

		System.out.println("Please enter file name: ");
		String file_request = stdin.next();
		Request request = new Request("File request", socket.getLocalAddress().toString(), new Date());
		request.setFilename(file_request);
		sendMessage(request);

		try {
			size = (Integer) in.readObject();
			mybytearray = new byte[size];
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// receive file
		if (size > 0) {
			try {
				fos = new FileOutputStream(ctx.getDownload_dir() + "/" + file_request);
				bos = new BufferedOutputStream(fos);
				bytesRead = in.read(mybytearray, 0, mybytearray.length);
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
					bytesRead = in.read(mybytearray, current, (mybytearray.length - current));
					if (bytesRead >= 0)
						current += bytesRead;
				} catch (IOException e) {
					e.printStackTrace();
				}
			} while (current < mybytearray.length);
			try {
				bos.write(mybytearray, 0, current);
				bos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	/**
	 * This method requests a List of files from server.
	 * 
	 * @param socket
	 *            The socket to make the request through.
	 */
	private void requestFileList(Socket socket) {

		try {

			Request request = new Request("Listing", socket.getLocalAddress().toString(), new Date());
			sendMessage(request);
			// Deserialise / unmarshal response from server
			List response = (List) in.readObject();
			for (int i = 0; i < response.size(); i++) {
				System.out.println(response.get(i));
			} // Deserialise
		} catch (Exception e) { // Deal with the error here. A try/catch stops a
								// programme crashing on error
			System.out.println("Error: " + e.getMessage());
		} // End of try /catch

	}
}// End of class
