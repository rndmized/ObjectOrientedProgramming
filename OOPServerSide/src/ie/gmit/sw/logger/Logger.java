package ie.gmit.sw.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import ie.gmit.sw.request.Request;



public class Logger implements Runnable{
	
	private BlockingQueue<Request> queue;
	private FileWriter fw;
	private volatile boolean keepRunning;
	
	public Logger(ArrayBlockingQueue<Request> queue) {
		this.queue = queue;
		keepRunning = true;
		try {
			fw = new FileWriter(new File("log.txt"),true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void run() {
		while(keepRunning){
			Request req;
			try {
				req = queue.take();
				try {
					fw = new FileWriter(new File("log.txt"),true);
					System.out.println("LOGGING: " + req.toString());
					fw.write(req.toString()+"\n");
					fw.close();
				} catch (IOException e) {
					System.err.println("Couldn't log: " + req.toString());
					e.printStackTrace();
				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}	
		}
	}
	
	public void log(Request request){
			/*[INFO | ERROR | WARNING] <command> requested by <client ip address> at <date time>*/
		try {
			queue.put(request);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
