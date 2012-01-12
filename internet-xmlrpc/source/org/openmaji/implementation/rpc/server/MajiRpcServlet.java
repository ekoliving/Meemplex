/*
 * Created on 19/08/2004
 *
 */
package org.openmaji.implementation.rpc.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.Principal;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.common.XmlRpcHttpRequestConfig;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.RequestProcessorFactoryFactory;
import org.apache.xmlrpc.server.AbstractReflectiveHandlerMapping.AuthenticationHandler;
import org.apache.xmlrpc.server.RequestProcessorFactoryFactory.RequestProcessorFactory;
import org.apache.xmlrpc.webserver.ServletWebServer;
import org.apache.xmlrpc.webserver.XmlRpcServletServer;

import org.openmaji.implementation.security.auth.LoginHelper;

//import sun.misc.BASE64Decoder;

/**
 * 
 * Currently we force the requirement for client credentials.
 * Username and password is required.
 * 
 * @author Warren Bloomer
 *
 */
public class MajiRpcServlet extends HttpServlet {
	private static final long serialVersionUID = 0L;

	private static final Logger logger = Logger.getAnonymousLogger();

	public static final String PARAM_SESSION_TIMEOUT = "sessionTimeout";

	public static final String PARAM_TRACE = "trace";
	
	private boolean trace = true;
	
	private String        servletInfo = "Maji RPC Servlet";
	
	private String        realm       = "Maji RPC";
	
	private XmlRpcServletServer xmlRpcServer;
	

	private RpcHandler handler = new RpcHandler();
	
	private TestHandler testHandler = new TestHandler();

	/* (non-Javadoc)
	 * @see javax.servlet.Servlet#getServletInfo()
	 */
	public String getServletInfo() {
		return servletInfo;
	}


	/* (non-Javadoc)
	 * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		// check for session timeout parameter
		String initParameter = config.getInitParameter(PARAM_SESSION_TIMEOUT);
		if (initParameter != null) {
			long sessionTimeout = Long.parseLong(initParameter);
			handler.setSessionTimeout(sessionTimeout);
		}

		// check for trace parameter
		String traceValue = config.getInitParameter(PARAM_TRACE);
		if (traceValue != null) {
			trace = Boolean.parseBoolean(traceValue);
		}
		
		// set log tracing on the RpcHandler
		this.handler.setTrace(trace);
		
		// create XML RPC server
		xmlRpcServer = new XmlRpcServletServer();

		PropertyHandlerMapping phm = new PropertyHandlerMapping();
		phm.setVoidMethodEnabled(true);
		phm.setRequestProcessorFactoryFactory(new RpcRequestProcessorFactoryFactory(handler, testHandler));
		phm.setTypeConverterFactory(xmlRpcServer.getTypeConverterFactory());
		phm.setAuthenticationHandler(new MyAuthenticationHandler());
		try {
			phm.addHandler("rpc", RpcHandler.class);
			phm.addHandler("test", TestHandler.class);
		}
		catch (XmlRpcException e) {
			throw new ServletException("Could not initialise servlet", e);
		}

		xmlRpcServer.setHandlerMapping(phm);

		// start handler session monitoring
		handler.start();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Servlet#destroy()
	 */
	public void destroy() {
		// cleanup and destroy XML RPC server
		xmlRpcServer = null;
		
		handler.stop();
		handler.cleanup();
	}
	
	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) 
		throws ServletException, IOException 
	{
		if (trace) {
			logger.info("doing post");
		}
		
		// TODO this is a hack to allow cross-domain requests
		response.setHeader("Access-Control-Allow-Origin", "*");

		// TODO perhaps we should only allow secure connections
//		if (httpRequest.isSecure()) {
//			LogTools.trace(logger, 20, "Connection is secure");			
//		}
//		else {
//			LogTools.trace(logger, 20, "Warning connection is NOT secure");
//		}

		// authenticate
		
		// TODO check for client certificate, and add that to the subject	
		
		LoginContext loginContext = null;
	
		// get any client certificate
//		Principal certPrincipal = authenticateCert(httpRequest);
		
		if (loginContext == null) {
			try {
				// TODO temporarily disabled authentication
				loginContext = authenticateBasic(request);
				
				// HACK to allow guest user
				// TODO remove hack
				if (loginContext == null) {
					loginContext = LoginHelper.login("guest", "guest99");
				}
			}
			catch (LoginException e) {
				if (trace) {
					logger.info("could not log in: " + e);
				}
				sendAuthChallenge(response);
				return;
			}
		}
		
		// login is required
		if (loginContext == null) {
			if (trace) {
				logger.info("login context is null");
			}
			sendAuthChallenge(response);
			return;
		}
		
		// TODO attach client certificate Principal to Subject if attained

		PrivilegedExceptionAction<Void> privilegedAction = new PrivilegedExceptionAction<Void>() {
			public Void run() 
				throws IOException
			{
				try {
					// execute the request
					xmlRpcServer.execute(request, response);
				}
				catch (SecurityException ex) {
					// send an "authorisation required" response
					sendAuthChallenge(response);
				}
				catch(IOException e) {
					logger.info("Problem writing response to the client. Client probably disconnected. " + e.getMessage());
					// TODO we might likely lose contents of a "receive" request.  
					// Do we need to push the event back on the front of the message queue
				}
				catch (Exception ex) {
					logger.info("Unhandled Exception while processing XML-RPC request: " + ex);
				}
				return null;
			}
		};

		try {
			Subject subject = loginContext.getSubject();
			Subject.doAsPrivileged(subject, privilegedAction, null);
//			try {
//				loginContext.logout();
//			}
//			catch (LoginException ex) {
//				LogTools.info(logger, "Problem logging out", ex);				
//			}
		}
		catch (PrivilegedActionException ex) {
			Throwable cause = ex.getCause();
			if (cause instanceof IOException) {
				throw (IOException) cause;
			}
		}
	}

	@Override
	public void log(String message, Throwable t) {
		logger.log(Level.INFO, message, t);
	}
	
	@Override
	public void log(String message) {
		logger.log(Level.INFO, message);
	}

	
	/* ------------------------------------------------ */
	
	/**
	 * 
	 * @param authString
	 * @throws SecurityException
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

		
		byte[] buf = DatatypeConverter.parseBase64Binary(authString.substring(BASIC.length()));
		String credentials = new String(buf);
		
		int i = credentials.indexOf(':');
		String username = credentials.substring(0, i);
		String password = credentials.substring(i+1);
		
//		LogTools.info(logger, "logging in uid=" + username + ", pwd=" + password);

		// get Subject
		loginContext = LoginHelper.login(username, password);
		
		return loginContext;
	}
	
	/**
	 * Return a Principal for the certificate
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

		if (trace) {
			logger.info(writer.toString());
		}

		// TODO return subject containing the certificate
		return null;
	}
	
	/**
	 * 
	 * @param httpResponse
	 * @throws IOException
	 */
	private void sendAuthChallenge(HttpServletResponse httpResponse) 
		throws IOException
	{
		if (trace) {
			logger.info("sending auth challenge");
		}
		
//		httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		httpResponse.setHeader("WWW-Authenticate", "Basic realm=\"" + realm + "\"");
		httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization Required");

//		PrintWriter writer = httpResponse.getWriter();
//		writer.println("Authorization Required");
//		writer.close();
//		httpResponse.flushBuffer();		
	}
	
	
	private class RpcRequestProcessorFactoryFactory implements RequestProcessorFactoryFactory {
		private Map<String, RequestProcessorFactory> factories = new HashMap<String, RequestProcessorFactory>();

		public RpcRequestProcessorFactoryFactory(RpcHandler handler, TestHandler testHandler) {
			factories.put(handler.getClass().getName(), new ObjectRequestProcessorFactory(handler));
			factories.put(testHandler.getClass().getName(), new ObjectRequestProcessorFactory(testHandler));
		}

		public RequestProcessorFactory getRequestProcessorFactory(Class aClass) throws XmlRpcException {
			if (trace) {
				logger.info("getting handler factory for : " + aClass);
			}
			return factories.get(aClass.getName());
		}

	
	}
	
	private class ObjectRequestProcessorFactory implements RequestProcessorFactory {
		Object singleton;
		public ObjectRequestProcessorFactory(Object singleton) {
			this.singleton = singleton;
        }
		
		public Object getRequestProcessor(XmlRpcRequest xmlRpcRequest) throws XmlRpcException {
			if (trace) {
				logger.info("getting request processor for : " + singleton.getClass() + " --- " + xmlRpcRequest.getMethodName());
			}
			return singleton;
		}
	}
	
    private class MyAuthenticationHandler implements AuthenticationHandler {
        public boolean isAuthorized(XmlRpcRequest pRequest){
            XmlRpcHttpRequestConfig config = (XmlRpcHttpRequestConfig) pRequest.getConfig();
            
            if (trace) {
            	logger.info("authenticating: " + config.getBasicUserName());
            }
            
            //return doAuthentication(config.getBasicUserName(), config.getBasicPassword());
            return true;
        }
    }

    private static final int port = 8000;

    public static void main(String[] args) throws Exception {
    	MajiRpcServlet servlet = new MajiRpcServlet();
        ServletWebServer webServer = new ServletWebServer(servlet, port);
        webServer.start();
    }

}
