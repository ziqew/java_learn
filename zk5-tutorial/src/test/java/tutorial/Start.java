package tutorial;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
public class Start {
	public static void main(String[] args){
		
		try{
            int port=8888;
            final String WEBAPPDIR = "src/main/webapp";

            final Server server = new Server(port);

            final String CONTEXTPATH = "/zk5-tutorial";

            WebAppContext bb = new WebAppContext();
            bb.setServer(server);
            bb.setContextPath(CONTEXTPATH);
            bb.setWar(WEBAPPDIR);
            server.addHandler(bb);


            server.start();
            server.join();
        }catch(Exception ex){
            ex.printStackTrace();
        }
	}
}
