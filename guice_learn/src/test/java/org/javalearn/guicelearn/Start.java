package org.javalearn.guicelearn;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.naming.Context;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: gongwenwei
 * Date: 12-8-24
 * Time: 下午10:04
 * To change this template use File | Settings | File Templates.
 */
public class Start {
    public static int httpServerPort=9999;

    public static void main(String []args) throws Exception{
        final String WEBAPPDIR = "src/main/webapp";
        final String CONTEXTPATH = "/guicelearn";

        System.setProperty("org.eclipse.jetty.LEVEL", "DEBUG");
        Server server = new Server(httpServerPort);

        WebAppContext webapp = new WebAppContext();
        webapp.setServer(server);
        webapp.setContextPath(CONTEXTPATH);
        webapp.setWar(WEBAPPDIR);
        server.setHandler(webapp);
        webapp.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
                ".*/.*jsp-api-[^/]*\\.jar$|.*/.*jsp-[^/]*\\.jar$|.*/.*taglibs[^/]*\\.jar$");

        server.start();
        server.join();
    }
}
