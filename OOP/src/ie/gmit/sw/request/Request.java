package ie.gmit.sw.request;

import java.io.Serializable;
import java.util.Date;
/**
 * This class implements a basic request to be made to a server.
 * @author RnDMizeD
 * @version 1.0
 */
public class Request implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String status;
	private String command;
	private String host;
	private Date date;
	private String filename;
	
	/**
	 * Default constructor
	 */
	public Request() {
		super();
	}
/**
 * Construct Request with a given set of values.
 * @param command to be executed on the server.
 * @param host where the request comes from.
 * @param date and time of he request.
 */
	public Request(String command, String host, Date date) {
		super();
		this.command = command;
		this.host = host;
		this.date = date;
	}
/**
 * 
 * @return current filename.
 */
	public String getFilename() {
		return filename;
	}
/**
 * 
 * @param filename to set.
 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
/**
 * 
 * @return current status.
 */
	public String getStatus() {
		return status;
	}
/**
 * 
 * @param status to set.
 */
	public void setStatus(String status) {
		this.status = status;
	}
/**
 * 
 * @return current command
 */
	public String getCommand() {
		return command;
	}
/**
 * 
 * @param command to set.
 */
	public void setCommand(String command) {
		this.command = command;
	}
/**
 * 
 * @return current host.
 */
	public String getHost() {
		return host;
	}
/**
 * 
 * @param host to set.
 */
	public void setHost(String host) {
		this.host = host;
	}
/**
 * 
 * @return current date.
 */
	public Date getDate() {
		return date;
	}
/**
 * 
 * @param date to set.
 */
	public void setDate(Date date) {
		this.date = date;
	}
	/**
	 * Returns a string representation of the object. In this case:
	 * 	[STATUS] COMMAND requested by HOST at DATE/TIME
	 */
	@Override
	public String toString() {
		/*[INFO | ERROR | WARNING] <command> requested by <client ip address> at <date time>*/
		return "[" + status + "]" + command + " requested by " + host + " at " + date;
	}

}
