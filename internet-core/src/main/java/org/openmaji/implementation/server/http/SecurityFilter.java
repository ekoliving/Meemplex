package org.openmaji.implementation.server.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.Principal;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmaji.implementation.security.auth.LoginHelper;

/**
 * 
 * @author stormboy
 *
 */
public class SecurityFilter implements Filter {
	
	private static final Logger logger = Logger.getLogger(SecurityFilter.class.getName());
	
	private String realm = "Meem";
	
	//private FilterConfig filterConfig;

	/**
	 * Initialise the filter
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		//this.filterConfig = filterConfig;
	}

	/**
	 * Destroy the filter
	 */
	public void destroy() {
		//this.filterConfig = null;
	}

	/**
	 * process the request and response with this filter.
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
		throws IOException, ServletException 
	{
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		HttpServletResponse httpResponse = (HttpServletResponse)response;
		process(httpRequest, httpResponse, chain);
	}

	/**
	 * Process the request/response
	 * 
	 * @param request
	 * @param response
	 * @param chain
	 * @throws IOException
	 */
	private void process(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
		throws IOException 
	{
		LoginContext loginContext = null;
		try {
			loginContext = authenticateBasic(request);
			
			// HACK to allow guest user
			// TODO remove hack
			if (loginContext == null) {
				loginContext = LoginHelper.login("guest", "guest99");
			}
		}
		catch (LoginException ex) {
			sendAuthChallenge(response);
			return;
		}
	
		// login is required
		if (loginContext == null) {
			sendAuthChallenge(response);
			return;
		}

		PrivilegedExceptionAction<Void> privilegedAction = new PrivilegedExceptionAction<Void>() {
			public Void run() throws IOException {
				try {
					chain.doFilter(request, response);
				}
				catch (SecurityException ex) {
					// send an "authorisation required" response
					sendAuthChallenge(response);
				}
				catch(IOException e) {
					logger.log(Level.INFO, "Problem writing response to the client. Client probably disconnected. " + e.getMessage());
					// TODO we might likely lose contents of a "receive" request.  
					// Do we need to push the event back on the front of the message queue
				}
				catch (Exception ex) {
					logger.log(Level.INFO, "Unhandled Exception while processing XML-RPC request: " + ex);
				}
				return null;
			}
		};

		try {
			Subject subject = loginContext.getSubject();
			Subject.doAsPrivileged(subject, privilegedAction, null);
		}
		catch (PrivilegedActionException ex) {
			Throwable cause = ex.getCause();
			if (cause instanceof IOException) {
				throw (IOException) cause;
			}
		}
	}

	/**
	 * Create login context for the Basic Auth credentials.
	 * Return null if no credentials are given
	 * 
	 * @param request
	 * @return
	 * @throws LoginException
	 * @throws IOException
	 */
	private LoginContext authenticateBasic(HttpServletRequest request) 
		throws LoginException, IOException
	{
		final String BASIC = "Basic ";
		
		LoginContext loginContext = null;
		
		String authString = request.getHeader("Authorization");		
		if (authString == null) {
			// null Subject
			return loginContext;
		}

		byte[] buf = javax.xml.bind.DatatypeConverter.parseBase64Binary(authString.substring(BASIC.length()));
		String credentials = new String(buf);
		
		int i = credentials.indexOf(':');
		String username = credentials.substring(0, i);
		String password = credentials.substring(i+1);
		
		// get Subject
		loginContext = LoginHelper.login(username, password);
		
		return loginContext;
	}
	
	/**
	 * Sends an authorisation challenge response.
	 * 
	 * @param httpResponse
	 * @throws IOException
	 */
	private void sendAuthChallenge(HttpServletResponse httpResponse) 
		throws IOException
	{
		httpResponse.setHeader("WWW-Authenticate", "Basic realm=\"" + realm + "\"");
		httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization Required");
	}

	/**
	 * Return a Principal for the certificate
	 * 
	 * @param request
	 */
	protected Principal authenticateCert(HttpServletRequest request) {
		StringWriter writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		
		pw.println("<PRE>");

		Principal userPrincipal = request.getUserPrincipal();
		pw.println("Principal: " + userPrincipal);
		
		Object certificate = request.getAttribute("javax.servlet.request.X509Certificate");
		pw.println("X509Certificate: " + certificate);

		
		// Display the cipher suite in use
		String cipherSuite = (String) request.getAttribute("javax.net.ssl.cipher_suite");
		pw.println("Cipher Suite: " + cipherSuite);

		// Display the client's certificates, if there are any
		if (cipherSuite != null) {
		  X509Certificate certChain[] = (X509Certificate[]) request.getAttribute("javax.net.ssl.peer_certificates");
		  if (certChain != null) {
		    for (int i = 0; i < certChain.length; i++) {
		    	pw.println ("Client Certificate [" + i + "] = " + certChain[i].toString());
		    }
		  }
		}

		pw.println ("</PRE>");
		pw.flush();

		logger.log(Level.INFO, writer.toString());

		// TODO return subject containing the certificate
		return null;
	}
	
}
