/*
 * Created on 19/08/2004
 *
 */
package org.openmaji.implementation.rpc.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.common.XmlRpcHttpRequestConfig;
import org.apache.xmlrpc.server.AbstractReflectiveHandlerMapping;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.RequestProcessorFactoryFactory;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.server.AbstractReflectiveHandlerMapping.AuthenticationHandler;
import org.apache.xmlrpc.webserver.ServletWebServer;
import org.apache.xmlrpc.webserver.XmlRpcServlet;

import org.openmaji.implementation.security.auth.LoginHelper;

/**
 * 
 * Currently we force the requirement for client credentials.
 * Username and password is required.
 * 
 * @author Warren Bloomer
 *
 */
public class TestRpcServlet extends XmlRpcServlet {

	private static final Logger logger = Logger.getAnonymousLogger();

	public static final String PARAM_SESSION_TIMEOUT = "sessionTimeout";

	private ServletConfig config;
	private String        servletInfo = "Maji RPC Servlet";
	private String        realm       = "Maji RPC";

	/**
	 * Single handler
	 */
	private TestHandler handler = new TestHandler();
	
	void x() {
		//super.
	}
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
		
//		logger.log(Level.INFO, "logging in uid=" + username + ", pwd=" + password);

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

		logger.log(Level.INFO, writer.toString());

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
//		httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		httpResponse.setHeader("WWW-Authenticate", "Basic realm=\"" + realm + "\"");
		httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization Required");

//		PrintWriter writer = httpResponse.getWriter();
//		writer.println("Authorization Required");
//		writer.close();
//		httpResponse.flushBuffer();		
	}
	
	
	
    /**
     * 
     */
    protected XmlRpcHandlerMapping newXmlRpcHandlerMapping() throws XmlRpcException {
    	logger.log(Level.INFO, "newXmlRpcHandlerMapping");
    	
        PropertyHandlerMapping mapping = (PropertyHandlerMapping) super.newXmlRpcHandlerMapping();
    	//PropertyHandlerMapping mapping = newPropertyHandlerMapping(url);
        
        AbstractReflectiveHandlerMapping.AuthenticationHandler authenticationHandler = new MyAuthenticationHandler();
        mapping.setAuthenticationHandler(authenticationHandler);
        
        return mapping;
    }
    
    protected PropertyHandlerMapping newPropertyHandlerMapping(URL url) throws IOException, XmlRpcException {
    	logger.log(Level.INFO, "newPropertyHandlerMapping");
    	
    	PropertyHandlerMapping mapping = super.newPropertyHandlerMapping(url);
    	
        mapping.setRequestProcessorFactoryFactory(new RpcRequestProcessorFactoryFactory(handler));
        mapping.load(Thread.currentThread().getContextClassLoader(), url);
 
    	return mapping;
    }

    private class MyAuthenticationHandler implements AuthenticationHandler {
        public boolean isAuthorized(XmlRpcRequest pRequest){
            XmlRpcHttpRequestConfig config = (XmlRpcHttpRequestConfig) pRequest.getConfig();
            
            logger.log(Level.INFO, "authenticating: " + config.getBasicUserName());
            
            //return isAuthenticated(config.getBasicUserName(), config.getBasicPassword());
            return true;
        }
    }
    
	public class RpcRequestProcessorFactoryFactory implements RequestProcessorFactoryFactory {
		private final RequestProcessorFactory factory = new RpcRequestProcessorFactory();

		private final TestHandler handler;

		public RpcRequestProcessorFactoryFactory(TestHandler handler) {
			this.handler = handler;
		}

		public RequestProcessorFactory getRequestProcessorFactory(Class aClass) throws XmlRpcException {
			return factory;
		}

		private class RpcRequestProcessorFactory implements RequestProcessorFactory {
			public Object getRequestProcessor(XmlRpcRequest xmlRpcRequest) throws XmlRpcException {
				return handler;
			}
		}
	}
	
    /**
     * Test Server
     */
    private static final int port = 8000;

    public static void main(String[] args) throws Exception {
        XmlRpcServlet servlet = new TestRpcServlet();
        ServletWebServer webServer = new ServletWebServer(servlet, port);
        webServer.start();
    }

}
