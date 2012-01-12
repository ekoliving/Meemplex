package org.openmaji.implementation.rpc.server;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class MajiRpcFilter implements Filter {

	public void init(FilterConfig config) throws ServletException {
	}
	
	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		// allow access from any origin
		((HttpServletResponse)response).setHeader("Access-Control-Allow-Origin", "*");
		filterChain.doFilter(request, response);
	}
}
