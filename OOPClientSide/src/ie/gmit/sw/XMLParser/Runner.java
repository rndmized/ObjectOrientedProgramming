package ie.gmit.sw.XMLParser;

public class Runner {
	public static void main(String[] args) throws Throwable{
		Context ctx = new Context();
		ContextParser cp = new ContextParser(ctx);
		cp.init();
		
		System.out.println(ctx);
	}
}
