package ie.gmit.sw.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import ie.gmit.sw.request.PoisonRequest;
import ie.gmit.sw.request.Request;
/**
 * This class logs requests into a file.
 * @author RnDMizeD
 * @version 1.0
 */
public class Logger implements Runnable {

	private BlockingQueue<Request> queue;
	private FileWriter fw;
	private volatile boolean keepRunning;
/**
 * Construct a Logger with a blocking queue.
 * @param queue queue containing the requests to be logged.
 */
	public Logger(ArrayBlockingQueue<Request> queue) {
		this.queue = queue;
		keepRunning = true;
	}
/**
 * While this task is active it will take requests from the queue and log them into a file.
 */
	@Override
	public void run() {
		while (keepRunning) {
			Request req;
			try {
				/*
				 * Take request from queue, if it is a poison request finish loop else...
				 */
				req = queue.take();
				if (req instanceof PoisonRequest) {
					keepRunning = false;
				} else {
					/*
					 * open log file and append request into it.
					 */
					try {
						fw = new FileWriter(new File("log.txt"), true);
						System.out.println("LOGGING: " + req.toString());
						fw.write(req.toString() + "\n");
						fw.close();
					} catch (IOException e) {
						System.err.println("Couldn't log: " + req.toString());
						e.printStackTrace();
					}
				}
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
/**
 * This method takes a request and puts it into the queue.
 * @param request
 */
	public void log(Request request) {
		try {
			queue.put(request);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
