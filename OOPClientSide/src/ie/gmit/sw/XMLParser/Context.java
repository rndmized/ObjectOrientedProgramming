package ie.gmit.sw.XMLParser;

/* A context represents the entire scope of an application, i.e.
 * we can assign "global variables" to a context.
 * 
 * This is a "bean class", containing a constructor and accessor
 * methods only.
 */
public class Context {
	public static final String CONF_FILE = "src/ie/gmit/sw/resources/conf.xml";
	private String host;
	private int port;
	private String download_dir;
	private String username;

	public Context() {
		super();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getDownload_dir() {
		return download_dir;
	}

	public void setDownload_dir(String download_dir) {
		this.download_dir = download_dir;
	}

	@Override
	public String toString() {
		return "Context [host=" + host + ", port=" + port + ", download-dir=" + download_dir + ", username=" + username
				+ "]";
	}
}
