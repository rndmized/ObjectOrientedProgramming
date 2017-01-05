package ie.gmit.sw.gui;

import ie.gmit.sw.WebClient;
import ie.gmit.sw.XMLParser.Context;
import ie.gmit.sw.XMLParser.ContextParser;

public class ClientRunner {
	
	public static void main(String[] args) {
		
		Context ctx = new Context();
		ContextParser cp = new ContextParser(ctx);
		try {
			cp.init();
		} catch (Throwable e) {
			e.printStackTrace();
		}	
		
		WebClient client = new WebClient(ctx);
		Thread t = new Thread(client, ctx.getUsername());
		t.start();
		
	}

}
