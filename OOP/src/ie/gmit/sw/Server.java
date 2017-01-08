package ie.gmit.sw;

import java.io.BufferedInputStream;

/* This class provides a very simple implementation of a web server. As a web server
 * must be capable of handling multiple requests from web browsers at the same time,
 * it is essential that the server is threaded, i.e. that the web server can perform
 * tasks in parallel and serially (one request at a time, after another).
 * 
 * In programming languages, all network communication is handled using sockets. A 
 * socket is a software abstraction of a connection between one computer on a network
 * and another. A server-socket is a process that listens on a port number for 
 * incoming client requests. For example, the standard port number for a HTTP server (a
 * web server) is port 80. Most of the commonly used Java networking classes are 
 * available in the java.net package. The java.io package contains a set of classes
 * designed to handle Input/Output (I/O) activity. We will use both packages in the web
 * server class below.  
 */

//Contains classes for all kinds of I/O activity
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
//Contains basic networking classes
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import ie.gmit.sw.file.FolderReader;
import ie.gmit.sw.logger.Logger;
import ie.gmit.sw.request.Request;

/**
 * This class implements a simple multi-threaded file server.
 * 
 * @author RnDMizeD
 * @version 1.0
 */
public class Server {

	private ServerSocket ss;
	private String path;
	private Logger logger;
	private ArrayBlockingQueue<Request> queue;
	private volatile boolean keepRunning = true;

	/**
	 * This constructor constructs a Server with a given port and path.
	 * 
	 * @param serverPort
	 *            Sets the port where the socket will be running.
	 * @param path
	 *            Sets the path for the files that will be available to
	 *            download.
	 */
	private Server(int serverPort, String path) {
		try {

			/*
			 * Create the socket
			 */
			ss = new ServerSocket(serverPort);
			/*
			 * Assign path value
			 */
			this.path = path;

			/*
			 * Create a new Thread and give it a new instance of Listener and an
			 * adequate name
			 */
			Thread server = new Thread(new Listener(), "Web Server Listener");

			/*
			 * Assign queue a valid instance, and pass it to a new Logger.
			 */
			queue = new ArrayBlockingQueue<Request>(7);
			this.logger = new Logger(queue);
			/*
			 * Create a new separate thread to run the logger on.
			 */
			Thread loggerThread = new Thread(logger, "Logger");
			/*
			 * Suggest the scheduler a higher priority for the server thread and
			 * start the threads.
			 */
			server.setPriority(Thread.MAX_PRIORITY);
			server.start();
			loggerThread.start();
			System.out.println("Server started and listening on port " + serverPort);
		} catch (IOException e) {
			System.out.println("Yikes! Something bad happened..." + e.getMessage());
		}
	}

	/**
	 * This is the main method. It will fire the constructor of the server.
	 * 
	 * @param args
	 *            port and path to files i.e. ie.gmit.sw.Server 7777
	 *            /path/to/myfiles
	 */
	public static void main(String[] args) {
		new Server(Integer.parseInt(args[0]), args[1]);
	}

	/**
	 * This inner class waits for a connection and spawns a new Thread to deal
	 * with it while it waits for a new connection.
	 * 
	 * @author RnDMizeD
	 * 
	 */
	private class Listener implements Runnable {

		public void run() {
			int counter = 0; // A counter to track the number of requests
			while (keepRunning) {
				try {

					Socket s = ss.accept(); // This is a blocking method,
											// causing this thread to stop and
											// wait here for an incoming request

					new Thread(new ClientThread(s), "T-" + counter).start();
					counter++;
				} catch (IOException e) {
					System.out.println("Error handling incoming request..." + e.getMessage());
				}
			}
		}
	}// End of inner class Listener

	/**
	 * This class takes the client socket and implements the interaction
	 * (Requests/Response) between the Server and the Client.
	 * 
	 * @author RnDMizeD
	 *
	 */
	private class ClientThread implements Runnable {
		private Socket sock;
		private ObjectInputStream in;
		private ObjectOutputStream out;

		/**
		 * Taking the client socket as a constructor enables the Listener class
		 * to farm out the request quickly
		 * 
		 * @param request
		 *            sets the socket to the socket request.
		 */
		private ClientThread(Socket request) {
			this.sock = request;

		}

		public void run() {
			boolean finish = false;

			try {

				in = new ObjectInputStream(sock.getInputStream());
				out = new ObjectOutputStream(sock.getOutputStream());

				do {

					try {
						System.out.println("Waiting for order.");
						Request command = (Request) in.readObject(); // Deserialise
																		// the
						// request
						// into an Object
						if (command.getCommand().equals("Listing")) {
							this.listing(command);
						} else if (command.getCommand().equals("Connection")) {
							this.connection(command);
						} else if (command.getCommand().equals("File request")) {
							this.requestFile(command);
						} else if (command.getCommand().equals(null)) {
							this.sock.close();
						}

					} catch (ClassNotFoundException classnot) {
						System.err.println("Data received in unknown format");
						System.err.println(classnot.getMessage());
					} catch (Exception e) {
						finish = true;
					}
				} while (!finish);
				System.out.println("Thread " + Thread.currentThread().getName() + " Finished.");

			} catch (

			Exception e) {
				e.printStackTrace();
			}
		}// end of run method

		/**
		 * This method serialises and sends an object.
		 * 
		 * @param obj
		 *            the object to serialise and send.
		 */
		private void sendMessage(Object obj) {
			try {
				out.writeObject(obj);
				out.flush();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}// end of sendMessage method

		/**
		 * This method takes a listing request and sends a response to the
		 * client with a list of files available.
		 * 
		 * @param command
		 *            the command requesting the file list.
		 */
		private void listing(Request command) {
			try {
				/*
				 * Get the list of files in the given path.
				 */
				FolderReader fr = new FolderReader(path);
				List<String> file_list = fr.getList();
				/*
				 * Set command status.
				 */
				command.setStatus("INFO");
				/*
				 * Send file List to client.
				 */
				sendMessage(file_list);
			} catch (Exception e) {
				/*
				 * Set command status.
				 */
				command.setStatus("ERROR");
			}
			logger.log(command);
		}// end of listing method

		/**
		 * This method takes a connection request and sends a response to the
		 * client accepting such connection.
		 * 
		 * @param command
		 *            the command requesting the connection.
		 */
		private void connection(Request command) {
			/*
			 * Set command status and sent request to logger to be deat with.
			 */
			command.setStatus("INFO");
			logger.log(command);
			/*
			 * Send Response
			 */
			String message = Thread.currentThread().getName() + ": Connection Successful.";
			sendMessage(message);
		}// end of connection method

		/**
		 * This method takes a file request and sends a response to the client
		 * sending such file.
		 * 
		 * @param command
		 *            the command requesting the file.
		 */
		private void requestFile(Request command) {
			FileInputStream fis;
			BufferedInputStream bis = null;
			try {
				/*
				 * Setting file path
				 */
				File myFile = new File(path + "/" + command.getFilename());
				/*
				 * Send file length to client
				 */
				sendMessage((int) myFile.length());

				/*
				 * Convert file to bytes and send it to the client
				 */
				byte[] mybytearray = new byte[(int) myFile.length()];
				fis = new FileInputStream(myFile);
				bis = new BufferedInputStream(fis);
				bis.read(mybytearray, 0, mybytearray.length);
				out.write(mybytearray, 0, mybytearray.length);
				out.flush();
				/*
				 * Set command status
				 */
				command.setStatus("INFO");
			} catch (IOException e) {
				e.printStackTrace();
				/*
				 * Set command status
				 */
				command.setStatus("ERROR");
			} finally {
				/*
				 * Format command for logging and send it to logger to log it.
				 */
				command.setCommand(command.getCommand() + " " + command.getFilename());
				logger.log(command);
				try {
					/*
					 * Close buffer.
					 */
					if (bis != null)
						bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}// End of requestFile Method
	}// End of inner class ClientThread
}// End of class SocketServer