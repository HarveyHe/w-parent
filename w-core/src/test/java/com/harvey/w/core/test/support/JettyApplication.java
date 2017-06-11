package com.harvey.w.core.test.support;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyApplication {
	private Server server;
	private int port = 8080;
	private String contextPath = "/";
	private String webappBase = "src/test/webapp";
	private WebAppContext webAppContext = new WebAppContext();

	public JettyApplication port(int port) {
		this.port = port;
		return this;
	}

	public JettyApplication contextPath(String contextPath) {
		this.contextPath = contextPath;
		return this;
	}

	public JettyApplication webappBase(String webappBase) {
		this.webappBase = webappBase;
		return this;
	}

	public WebAppContext getWebAppContext() {
		return this.webAppContext;
	}

	public Server getServer() {
		return this.server;
	}

	public void startup() throws Exception {
		if (server != null) {
			server.stop();
		} else {
			server = new Server(port);
			server.setStopAtShutdown(true);
			server.setHandler(webAppContext);
		}
		webAppContext.setContextPath(contextPath);
		//File file = new File(webappBase);
		//webAppContext.setWar(file.getAbsolutePath());
		webAppContext.setResourceBase(webappBase);
		webAppContext.setClassLoader(getClass().getClassLoader());
		server.start();
		while(Server.STARTING.equals(server.getState())){
			Thread.sleep(1000);
		}
	}
	
	public boolean isRunning() {
	    return server != null && (Server.RUNNING.equals(server.getState()) || Server.STARTED.equals(server.getState()));
	}
	
    public void doLoop() throws InterruptedException {
        while(this.isRunning()) {
            Thread.sleep(1000L);
        }
    }	

	public void shutdown() throws Exception {
		if(server != null){
			server.stop();
		}
	}
}
