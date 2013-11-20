package org.openmaji.implementation.server.http;
import org.openmaji.meem.Facet;
import javax.servlet.Servlet;
public interface MajiServlet extends Facet{
	public void sendServlet(Servlet servlet);
}
