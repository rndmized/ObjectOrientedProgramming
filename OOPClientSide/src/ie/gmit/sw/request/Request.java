package ie.gmit.sw.request;

import java.io.Serializable;
import java.util.Date;

public class Request implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String status;
	private String command;
	private String host;
	private Date date;
	private String filename;
	
	
	public Request() {
		super();
	}

	public Request(String command, String host, Date date) {
		super();
		this.command = command;
		this.host = host;
		this.date = date;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	@Override
	public String toString() {
		/*[INFO | ERROR | WARNING] <command> requested by <client ip address> at <date time>*/
		return "[" + status + "]" + command + " requested by " + host + " at " + date;
	}

}
