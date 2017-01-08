package ie.gmit.sw.config;
/**
 * A context represents the entire scope of an application, i.e. we can assign "global variables" to a context.
 * This is a "bean class", containing a constructor and accessor methods only. 
 * @author RnDMizeD
 *
 */

public class Context {
	public static final String CONF_FILE = "src/ie/gmit/sw/config/conf.xml";
	private String host;
	private int port;
	private String download_dir;
	private String username;
/**
 * 
 * @return current username.
 */
	public String getUsername() {
		return username;
	}
/**
 * 
 * @param username to set.
 */
	public void setUsername(String username) {
		this.username = username;
	}
/**
 * 
 * @return current host address.
 */
	public String getHost() {
		return host;
	}
/**
 * 
 * @param host address to set.
 */
	public void setHost(String host) {
		this.host = host;
	}
/**
 * 
 * @return current port number.
 */
	public int getPort() {
		return port;
	}
/**
 * 
 * @param port number to set.
 */
	public void setPort(int port) {
		this.port = port;
	}
/**
 * 
 * @return current download directory.
 */
	public String getDownload_dir() {
		return download_dir;
	}
/**
 * 
 * @param download_dir location where downloads are to be stored.
 */
	public void setDownload_dir(String download_dir) {
		this.download_dir = download_dir;
	}
/**
 * Returns a string representation of the object values.
 */
	@Override
	public String toString() {
		return "Context [host=" + host + ", port=" + port + ", download-dir=" + download_dir + ", username=" + username
				+ "]";
	}
}
