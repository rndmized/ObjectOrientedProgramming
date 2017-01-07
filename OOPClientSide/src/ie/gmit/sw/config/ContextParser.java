package ie.gmit.sw.config;

import javax.xml.parsers.*;
import org.w3c.dom.*;

public class ContextParser {
	private Context ctx;

	public ContextParser(Context ctx) {
		super();
		this.ctx = ctx;
	}

	public void init() throws Throwable {
		/*
		 * These three lines are part of JAXP (Java API for XML Processing) and
		 * are designed to completely encapsulate how a DOM node tree in
		 * manufactured. The concrete classes that are doing the actual work are
		 * part of the Apache Xerces project. JAXP shields us from having to
		 * know and understand the complexity of Xerces through encapsulating
		 * the process.
		 */
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(Context.CONF_FILE);

		/*
		 * The type Document in the line above is an org.w3c.dom.Document. From
		 * this point on in the method, we will only use the DOM "STANDARD" to
		 * do the processing. DOM is language neutral and completely abstract.
		 * As a result, it is completely stable! The DOM standard hasn't changed
		 * much since the year 2000...! Abstractions are highly stable and can
		 * be relied upon.
		 */
		Element root = doc.getDocumentElement(); // Get the root of the node
													// tree
		NodeList children = root.getChildNodes(); // Get the child node of the
													// root

		if (root.getNodeName().equals("client-config")) { // Check if it is the
															// element
															// <client-config>
			NamedNodeMap atts = root.getAttributes(); // Get the attributes as a
														// map of name/value
														// pairs

			for (int j = 0; j < atts.getLength(); j++) { // Look over the
															// attributes. This
															// is similar to
															// looping over a
															// key set in a Map
				if (atts.item(j).getNodeName().equals("username")) { // get root
																		// attribute
																		// username
					ctx.setUsername(atts.item(j).getNodeValue());
				}
			}
		}

		// Find the elements database and driver
		for (int i = 0; i < children.getLength(); i++) { // Loop over the child
															// nodes
			Node next = children.item(i); // Get the next child

			if (next instanceof Element) { // Check if it is an element node.
											// There are 12 different types of
											// Node
				Element e = (Element) next; // Cast the general node to an
											// element node

				if (e.getNodeName().equals("server-host")) {
					/*
					 * The XML document contains the element <client-config>:
					 * <server-host>127.0.0.1</server-host> The text "127.0.0.1"
					 * is just parseable character data (PCDATA) and is
					 * contained as a child node (a text node) of the element
					 * node "server-hpst".
					 */

					String server_host = e.getFirstChild().getNodeValue(); // Read
																			// the
																			// text
																			// 127.0.0.1
																			// as
																			// a
																			// String

					/*
					 * The instance of 127.0.0.1(Server-host String) is assigned
					 * the context.
					 */
					ctx.setHost(server_host);
				} else if (e.getNodeName().equals("server-port")) {
					int server_port = Integer.parseInt(e.getFirstChild().getNodeValue());
					ctx.setPort(server_port);
				} else if (e.getNodeName().equals("download-dir")) {
					String download_dir = e.getFirstChild().getNodeValue();
					ctx.setDownload_dir(download_dir);
				}
			}
		}
	}

	public Context getCtx() {
		return ctx;
	}

	public void setCtx(Context ctx) {
		this.ctx = ctx;
	}
}
