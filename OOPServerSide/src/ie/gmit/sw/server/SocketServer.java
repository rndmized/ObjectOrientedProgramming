package ie.gmit.sw.server;

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

public class SocketServer {
	private ServerSocket ss; // A server socket listens on a port number for
								// incoming requests

	// The first 1024 ports require administrator privileges. We'll use 8080
	// instead. The range
	// of port numbers runs up to 2 ^ 16 = 65536 ports.
	private static final int SERVER_PORT = 7777;

	private Logger logger;

	// The boolean value keepRunning is used to control the while loop in the
	// inner class called
	// Listener. The volatile keyword tells the JVM not to cache the value of
	// keepRunning during
	// optimisation, but to check it's value in memory before using it.
	private volatile boolean keepRunning = true;

	// A null constructor for the WebServer class
	private SocketServer(int serverPort) {
		try { // Try the following. If anything goes wrong, the error will be
				// passed to the catch block

			ss = new ServerSocket(serverPort); // Start the server socket
												// listening on port 7777

			/*
			 * A Thread is a worker. A runnable is a job. We'll give the worker
			 * thread called "server" the job of handling incoming requests from
			 * clients. Note: calling start results in a new JVM stack being
			 * created. The run() method of the Thread or Runnable will be
			 * placed on the new stack and executed when the Thread Scheduler
			 * (consider this a cantankerous and uncommunicative part of the
			 * JVM) decides so. There is absolutely NO GUARANTEE of either order
			 * or execution time. We can however ask the Thread Scheduler
			 * (politely) to run a thread as a max, min or normal priority.
			 */
			Thread server = new Thread(new Listener(), "Web Server Listener"); // We
																				// can
																				// also
																				// name
																				// threads
			ArrayBlockingQueue<Request> queue = new ArrayBlockingQueue<Request>(7);
			this.logger = new Logger(queue);

			Thread loggerThread = new Thread(logger, "Logger");

			server.setPriority(Thread.MAX_PRIORITY); // Ask the Thread Scheduler
														// to run this thread as
														// a priority
			server.start(); // The Hollywood Principle - Don't call us, we'll
							// call you
			loggerThread.start();

			System.out.println("Server started and listening on port " + serverPort);

		} catch (IOException e) { // Something nasty happened. We should handle
									// error gracefully, i.e. not like this...
			System.out.println("Yikes! Something bad happened..." + e.getMessage());
		}
	}

	// A main method is required to start a standard Java application
	public static void main(String[] args) {
		new SocketServer(Integer.parseInt(args[0])); // Create an instance of a
														// WebServer. This fires
														// the
		// constructor of WebServer() above on the main
		// stack
	}

	/*
	 * The inner class Listener is a Runnable, i.e. a job that can be given to a
	 * Thread. The job that the class has been given is to intercept incoming
	 * client requests and farm them out to other threads. Each client request
	 * is in the form of a socket and will be handled by a separate new thread.
	 */
	private class Listener implements Runnable { // A Listener IS-A Runnable

		// The interface Runnable declare the method "public void run();" that
		// must be implemented
		public void run() {
			int counter = 0; // A counter to track the number of requests
			while (keepRunning) { // Loop will keepRunning is true. Note that
									// keepRunning is "volatile"
				try { // Try the following. If anything goes wrong, the error
						// will be passed to the catch block

					Socket s = ss.accept(); // This is a blocking method,
											// causing this thread to stop and
											// wait here for an incoming request

					/*
					 * If we get to this line, it means that a client request
					 * was received and that the socket "s" is a real network
					 * connection between some computer and this programme.
					 * We'll farm out this request to a new Thread (worker),
					 * allowing us to handle the next incoming request (we could
					 * have many requests hitting the server at the same time),
					 * so we have to be able to handle them quickly.
					 */
					new Thread(new ClientThread(s), "T-" + counter).start(); // Give
																				// the
																				// new
																				// job
																				// to
																				// the
																				// new
																				// worker
																				// and
																				// tell
																				// it
																				// to
																				// start
																				// work
					counter++; // Increment counter
				} catch (IOException e) { // Something nasty happened. We should
											// handle error gracefully, i.e. not
											// like this...
					System.out.println("Error handling incoming request..." + e.getMessage());
				}
			}
		}
	}// End of inner class Listener

	/*
	 * The inner class HTTPRequest is a Runnable, i.e. a job that can be given
	 * to a Thread. The job that the class has been given is to handle an
	 * individual client request, by reading information from the socket's input
	 * stream (bytes) and responding by sending information to the socket's
	 * output stream (more bytes).
	 */
	private class ClientThread implements Runnable {
		private Socket sock; // A specific socket connection between some
								// computer on a network and this programme
		private ObjectInputStream in;
		private ObjectOutputStream out;

		private ClientThread(Socket request) { // Taking the client socket as a
												// constructor enables the
												// Listener class above to farm
												// out the request quickly
			this.sock = request; // Assign to the instance variable sock the
									// value passed to the constructor.

		}

		// The interface Runnable declare the method "public void run();" that
		// must be implemented
		public void run() {
			boolean finish = false;
			String path = "downloads";

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
		}

		private void sendMessage(Object msg) {
			try {
				out.writeObject(msg);
				out.flush();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}

		private void listing(Request command) {
			String path = "downloads";
			try {
				FolderReader fr = new FolderReader(path);
				List<String> file_list = fr.getList();
				command.setStatus("INFO");
				logger.log(command);
				sendMessage(file_list);
			} catch (Exception e) {
				command.setStatus("ERROR");
				logger.log(command);
			}
		}

		private void connection(Request command) {
			String message = Thread.currentThread().getName() + ": Connection Successful.";
			command.setStatus("INFO");
			logger.log(command);
			sendMessage(message);
		}

		private void requestFile(Request command) {
			String path = "downloads";
			FileInputStream fis;
			BufferedInputStream bis = null;
			try {
				// send file
				File myFile = new File(path + "/" + command.getFilename());
				sendMessage((int) myFile.length());
				byte[] mybytearray = new byte[(int) myFile.length()];
				fis = new FileInputStream(myFile);
				bis = new BufferedInputStream(fis);
				bis.read(mybytearray, 0, mybytearray.length);
				out.write(mybytearray, 0, mybytearray.length);
				out.flush();
				command.setStatus("INFO");
				command.setCommand(command.getCommand() + " " + command.getFilename());
			} catch (IOException e) {
				e.printStackTrace();
				command.setStatus("ERROR");
			} finally {
				logger.log(command);
					try {
						if (bis != null)
							bis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				
			}
		}
	}// End of inner class HTTPRequest
}// End of class WebServer