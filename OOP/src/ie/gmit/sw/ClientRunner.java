package ie.gmit.sw;

import ie.gmit.sw.config.Context;
import ie.gmit.sw.config.ContextParser;

public class ClientRunner {
	
	public static void main(String[] args) {
		
		Context ctx = new Context();
		ContextParser cp = new ContextParser(ctx);
		try {
			cp.parse();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		SocketClient client = new SocketClient(ctx);
		Thread t = new Thread(client, ctx.getUsername());
		t.start();
		
	}

}
